import 'package:flutter/painting.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/app_colors.dart';

void main() {
  group('AppColors ramps', () {
    test('neutral ramp matches the modernist tokens', () {
      // Arrange
      const expected = <int, Color>{
        100: Color(0xFFF8F4F4),
        200: Color(0xFFEAE7E7),
        300: Color(0xFFD7D3D3),
        400: Color(0xFFBAB6B6),
        500: Color(0xFF9B9797),
        600: Color(0xFF7D7979),
        700: Color(0xFF605D5D),
        800: Color(0xFF444141),
        900: Color(0xFF2D2B2B),
      };

      // Act
      const actual = <int, Color>{
        100: AppColors.neutral100,
        200: AppColors.neutral200,
        300: AppColors.neutral300,
        400: AppColors.neutral400,
        500: AppColors.neutral500,
        600: AppColors.neutral600,
        700: AppColors.neutral700,
        800: AppColors.neutral800,
        900: AppColors.neutral900,
      };

      // Assert
      expect(actual, expected);
    });

    test('accent ramp matches the modernist tokens', () {
      // Arrange
      const expected = <int, Color>{
        100: Color(0xFFFFF2EF),
        200: Color(0xFFFFE0D9),
        300: Color(0xFFFFC4B8),
        400: Color(0xFFFF9783),
        500: Color(0xFFFF563C),
        600: Color(0xFFDD2B0F),
        700: Color(0xFFAE1800),
        800: Color(0xFF7C1405),
        900: Color(0xFF4D170E),
      };

      // Act
      const actual = <int, Color>{
        100: AppColors.accent100,
        200: AppColors.accent200,
        300: AppColors.accent300,
        400: AppColors.accent400,
        500: AppColors.accent500,
        600: AppColors.accent600,
        700: AppColors.accent700,
        800: AppColors.accent800,
        900: AppColors.accent900,
      };

      // Assert
      expect(actual, expected);
    });

    test('secondary accent ramp matches the modernist tokens', () {
      // Arrange
      const expected = <int, Color>{
        100: Color(0xFFFFF2EF),
        200: Color(0xFFFFE0DA),
        300: Color(0xFFFFC4B9),
        400: Color(0xFFFF9784),
        500: Color(0xFFEF6853),
        600: Color(0xFFC94B39),
        700: Color(0xFF9E3526),
        800: Color(0xFF71261B),
        900: Color(0xFF471D16),
      };

      // Act
      const actual = <int, Color>{
        100: AppColors.accentSecondary100,
        200: AppColors.accentSecondary200,
        300: AppColors.accentSecondary300,
        400: AppColors.accentSecondary400,
        500: AppColors.accentSecondary500,
        600: AppColors.accentSecondary600,
        700: AppColors.accentSecondary700,
        800: AppColors.accentSecondary800,
        900: AppColors.accentSecondary900,
      };

      // Assert
      expect(actual, expected);
    });
  });

  group('AppColors ground', () {
    test('light ground matches styles.css :root', () {
      // Arrange
      const expected = <String, Color>{
        'bg': Color(0xFFF3F2F2),
        'surface': Color(0xFFEAE9E9),
        'surfaceAlt': Color(0xFFDEDCDC),
        'text': Color(0xFF201E1D),
        'accent': Color(0xFFEC3013),
        'accentSecondary': Color(0xFFE15B47),
        'onAccent': Color(0xFFF3F2F2),
      };

      // Act
      const actual = <String, Color>{
        'bg': AppColors.lightBg,
        'surface': AppColors.lightSurface,
        'surfaceAlt': AppColors.lightSurfaceAlt,
        'text': AppColors.lightText,
        'accent': AppColors.lightAccent,
        'accentSecondary': AppColors.accentSecondary,
        'onAccent': AppColors.lightOnAccent,
      };

      // Assert
      expect(actual, expected);
    });

    test('dark ground matches the prototype token map', () {
      // Arrange
      const expected = <String, Color>{
        'bg': Color(0xFF1A1918),
        'surface': Color(0xFF262424),
        'surfaceAlt': Color(0xFF302E2E),
        'text': Color(0xFFF3F2F2),
        'accent': Color(0xFFFF563C),
        'onAccent': Color(0xFF1A1918),
      };

      // Act
      const actual = <String, Color>{
        'bg': AppColors.darkBg,
        'surface': AppColors.darkSurface,
        'surfaceAlt': AppColors.darkSurfaceAlt,
        'text': AppColors.darkText,
        'accent': AppColors.darkAccent,
        'onAccent': AppColors.darkOnAccent,
      };

      // Assert
      expect(actual, expected);
    });

    test('dark accent is the accent ramp 500 step, per the spec', () {
      // Arrange
      const expected = AppColors.accent500;

      // Act
      const actual = AppColors.darkAccent;

      // Assert
      expect(actual, expected);
    });
  });

  group('AppColors status', () {
    test('light status pairs match the prototype token map', () {
      // Arrange
      const expected = <String, Color>{
        'pendingBg': Color(0xFFFFF3DC),
        'pendingFg': Color(0xFFA66300),
        'approvedBg': Color(0xFFE1F3E7),
        'approvedFg': Color(0xFF1E7A46),
        'rejectedBg': Color(0xFFFFE0D9),
        'rejectedFg': Color(0xFFAE1800),
        'paidBg': Color(0xFF2D2B2B),
        'paidFg': Color(0xFFF3F2F2),
        'mutedBg': Color(0xFFEAE7E7),
        'mutedFg': Color(0xFF444141),
      };

      // Act
      const actual = <String, Color>{
        'pendingBg': AppColors.lightPendingBg,
        'pendingFg': AppColors.lightPendingFg,
        'approvedBg': AppColors.lightApprovedBg,
        'approvedFg': AppColors.lightApprovedFg,
        'rejectedBg': AppColors.lightRejectedBg,
        'rejectedFg': AppColors.lightRejectedFg,
        'paidBg': AppColors.lightPaidBg,
        'paidFg': AppColors.lightPaidFg,
        'mutedBg': AppColors.lightMutedBg,
        'mutedFg': AppColors.lightMutedFg,
      };

      // Assert
      expect(actual, expected);
    });

    test('dark status pairs match the prototype token map', () {
      // Arrange
      const expected = <String, Color>{
        'pendingBg': Color(0xFF3A2C12),
        'pendingFg': Color(0xFFF5B94D),
        'approvedBg': Color(0xFF14301F),
        'approvedFg': Color(0xFF5FD08D),
        'rejectedBg': Color(0xFF3A1A14),
        'rejectedFg': Color(0xFFFF7A62),
        'paidBg': Color(0xFFD7D3D3),
        'paidFg': Color(0xFF1A1918),
        'mutedBg': Color(0xFF302E2E),
        'mutedFg': Color(0xFFBAB6B6),
      };

      // Act
      const actual = <String, Color>{
        'pendingBg': AppColors.darkPendingBg,
        'pendingFg': AppColors.darkPendingFg,
        'approvedBg': AppColors.darkApprovedBg,
        'approvedFg': AppColors.darkApprovedFg,
        'rejectedBg': AppColors.darkRejectedBg,
        'rejectedFg': AppColors.darkRejectedFg,
        'paidBg': AppColors.darkPaidBg,
        'paidFg': AppColors.darkPaidFg,
        'mutedBg': AppColors.darkMutedBg,
        'mutedFg': AppColors.darkMutedFg,
      };

      // Assert
      expect(actual, expected);
    });

    test(
      'rejected foreground reuses the accent ramp so red never means fine',
      () {
        // Arrange
        const expected = AppColors.accent700;

        // Act
        const actual = AppColors.lightRejectedFg;

        // Assert
        expect(actual, expected);
      },
    );
  });

  group('AppColors skeleton', () {
    test('skeleton fills match the prototype token map in both themes', () {
      // Arrange
      const expected = <String, Color>{
        'light': Color(0xFFD7D3D3),
        'lightAlt': Color(0xFFE4E1E1),
        'dark': Color(0xFF3A3737),
        'darkAlt': Color(0xFF2E2C2C),
      };

      // Act
      const actual = <String, Color>{
        'light': AppColors.lightSkeleton,
        'lightAlt': AppColors.lightSkeletonAlt,
        'dark': AppColors.darkSkeleton,
        'darkAlt': AppColors.darkSkeletonAlt,
      };

      // Assert
      expect(actual, expected);
    });
  });
}
