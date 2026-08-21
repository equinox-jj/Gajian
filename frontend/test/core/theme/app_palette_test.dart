import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/app_colors.dart';
import 'package:gajian/core/theme/app_palette.dart';

void main() {
  group('AppPalette.light', () {
    test('binds the semantic roles to the light ground tokens', () {
      // Arrange
      const expected = <String, Color>{
        'bg': AppColors.lightBg,
        'surface': AppColors.lightSurface,
        'surfaceAlt': AppColors.lightSurfaceAlt,
        'text': AppColors.lightText,
        'accent': AppColors.lightAccent,
        'accentSecondary': AppColors.accentSecondary,
        'onAccent': AppColors.lightOnAccent,
        'skeleton': AppColors.lightSkeleton,
        'skeletonAlt': AppColors.lightSkeletonAlt,
      };

      // Act
      const p = AppPalette.light;

      // Assert
      expect(<String, Color>{
        'bg': p.bg,
        'surface': p.surface,
        'surfaceAlt': p.surfaceAlt,
        'text': p.text,
        'accent': p.accent,
        'accentSecondary': p.accentSecondary,
        'onAccent': p.onAccent,
        'skeleton': p.skeleton,
        'skeletonAlt': p.skeletonAlt,
      }, expected);
    });

    test('derives the divider from the ink at 40% alpha', () {
      // Arrange
      const expected = 0.40;

      // Act
      final divider = AppPalette.light.divider;

      // Assert
      expect(divider.a, closeTo(expected, 0.005));
    });

    test('binds the five status pairs to the light status tokens', () {
      // Arrange
      const expected = <String, Color>{
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

      // Act
      const p = AppPalette.light;

      // Assert
      expect(<String, Color>{
        'pendingBg': p.pendingBg,
        'pendingFg': p.pendingFg,
        'approvedBg': p.approvedBg,
        'approvedFg': p.approvedFg,
        'rejectedBg': p.rejectedBg,
        'rejectedFg': p.rejectedFg,
        'paidBg': p.paidBg,
        'paidFg': p.paidFg,
        'mutedBg': p.mutedBg,
        'mutedFg': p.mutedFg,
      }, expected);
    });
  });

  group('AppPalette.dark', () {
    test('binds the semantic roles to the dark ground tokens', () {
      // Arrange
      const expected = <String, Color>{
        'bg': AppColors.darkBg,
        'surface': AppColors.darkSurface,
        'surfaceAlt': AppColors.darkSurfaceAlt,
        'text': AppColors.darkText,
        'accent': AppColors.darkAccent,
        'onAccent': AppColors.darkOnAccent,
        'skeleton': AppColors.darkSkeleton,
        'skeletonAlt': AppColors.darkSkeletonAlt,
      };

      // Act
      const p = AppPalette.dark;

      // Assert
      expect(<String, Color>{
        'bg': p.bg,
        'surface': p.surface,
        'surfaceAlt': p.surfaceAlt,
        'text': p.text,
        'accent': p.accent,
        'onAccent': p.onAccent,
        'skeleton': p.skeleton,
        'skeletonAlt': p.skeletonAlt,
      }, expected);
    });

    test('derives the divider from the ink at 30% alpha', () {
      // Arrange
      const expected = 0.30;

      // Act
      final divider = AppPalette.dark.divider;

      // Assert
      expect(divider.a, closeTo(expected, 0.005));
    });
  });

  group('AppPalette contract', () {
    test('copyWith replaces only the roles it is given', () {
      // Arrange
      const replacement = Color(0xFF00FF00);

      // Act
      final actual = AppPalette.light.copyWith(accent: replacement);

      // Assert
      expect(
        (actual.accent, actual.bg, actual.pendingFg),
        (replacement, AppPalette.light.bg, AppPalette.light.pendingFg),
      );
    });

    test('lerp at t=0 and t=1 returns the endpoints', () {
      // Arrange
      const a = AppPalette.light;
      const b = AppPalette.dark;

      // Act
      final ends = (a.lerp(b, 0).accent, a.lerp(b, 1).accent);

      // Assert
      expect(ends, (a.accent, b.accent));
    });

    test('lerp midway sits between the two grounds', () {
      // Arrange
      const a = AppPalette.light;
      const b = AppPalette.dark;

      // Act
      final mid = a.lerp(b, 0.5);

      // Assert
      expect(mid.bg, Color.lerp(a.bg, b.bg, 0.5));
    });

    test('lerp against a foreign extension falls back to this palette', () {
      // Arrange
      const a = AppPalette.light;

      // Act
      final actual = a.lerp(null, 0.5);

      // Assert
      expect(actual, same(a));
    });

    test('two palettes built from the same tokens compare equal', () {
      // Arrange
      const a = AppPalette.light;

      // Act
      final b = AppPalette.light.copyWith();

      // Assert
      expect(b, a);
    });
  });

  group('BuildContext.palette', () {
    testWidgets('reads the palette the enclosing theme carries', (
      tester,
    ) async {
      // Arrange
      late AppPalette read;
      final theme = ThemeData(
        extensions: const <ThemeExtension<dynamic>>[AppPalette.dark],
      );

      // Act
      await tester.pumpWidget(
        MaterialApp(
          theme: theme,
          home: Builder(
            builder: (context) {
              read = context.palette;
              return const SizedBox.shrink();
            },
          ),
        ),
      );

      // Assert
      expect(read, AppPalette.dark);
    });
  });
}
