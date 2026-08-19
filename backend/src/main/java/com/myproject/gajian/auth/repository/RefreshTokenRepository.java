package com.myproject.gajian.auth.repository;

import com.myproject.gajian.auth.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Serializes concurrent rotations of the same token. Without the row lock two parallel refreshes
     * both observe {@code revokedAt == null} and each mint a replacement, forking one session into two
     * and hiding the reuse from theft detection.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM RefreshToken t WHERE t.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken t SET t.revokedAt = :revokedAt "
            + "WHERE t.user.id = :userId AND t.revokedAt IS NULL")
    int revokeAllByUserId(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

    /**
     * Breaks the rotation chain among the rows {@link #deleteExpiredAndRevoked} is about to remove —
     * the self-referencing {@code replaced_by_token_id} FK is checked per row, so deleting a whole
     * chain in one statement fails without this. Only rows already in the delete set are touched: a
     * row can only point at a token issued after it, so anything referencing a doomed row is doomed too.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken t SET t.replacedByToken = NULL "
            + "WHERE t.expiresAt < :now AND t.revokedAt IS NOT NULL")
    int clearReplacementLinksOfExpiredAndRevoked(@Param("now") Instant now);

    /**
     * Expired-but-unrevoked rows are deliberately kept: they are what lets a stolen token still be
     * recognised as reuse rather than merely expired.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now AND t.revokedAt IS NOT NULL")
    int deleteExpiredAndRevoked(@Param("now") Instant now);
}
