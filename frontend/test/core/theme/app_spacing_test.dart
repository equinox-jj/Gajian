import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/app_spacing.dart';

void main() {
  group('AppSpacing', () {
    test('is the 4px-base scale from styles.css', () {
      // Arrange
      const expected = <String, double>{
        'x1': 4,
        'x2': 8,
        'x3': 12,
        'x4': 16,
        'x6': 24,
        'x8': 32,
      };

      // Act
      const actual = <String, double>{
        'x1': AppSpacing.x1,
        'x2': AppSpacing.x2,
        'x3': AppSpacing.x3,
        'x4': AppSpacing.x4,
        'x6': AppSpacing.x6,
        'x8': AppSpacing.x8,
      };

      // Assert
      expect(actual, expected);
    });

    test('every step is a multiple of the 4px base', () {
      // Arrange
      const scale = <double>[
        AppSpacing.x1,
        AppSpacing.x2,
        AppSpacing.x3,
        AppSpacing.x4,
        AppSpacing.x6,
        AppSpacing.x8,
      ];

      // Act
      final remainders = scale.map((s) => s % AppSpacing.x1).toSet();

      // Assert
      expect(remainders, {0.0});
    });
  });

  group('AppRadius', () {
    test('is zero — the modernist rule, not a Material default', () {
      // Arrange
      const expected = BorderRadius.zero;

      // Act
      const actual = AppRadius.none;

      // Assert
      expect(actual, expected);
    });

    test('AppShape.rectangle carries no rounding', () {
      // Arrange
      const expected = BorderRadius.zero;

      // Act
      final actual = AppShape.rectangle.borderRadius;

      // Assert
      expect(actual, expected);
    });
  });

  group('AppSize', () {
    test('matches the touch targets in spec section 04', () {
      // Arrange
      const expected = <String, double>{
        'buttonPrimary': 52,
        'buttonSecondary': 48,
        'field': 48,
        'listRowMin': 64,
        'iconButton': 44,
        'tabBar': 56,
        'iconSm': 20,
        'iconMd': 24,
        'iconStroke': 2,
      };

      // Act
      const actual = <String, double>{
        'buttonPrimary': AppSize.buttonPrimary,
        'buttonSecondary': AppSize.buttonSecondary,
        'field': AppSize.field,
        'listRowMin': AppSize.listRowMin,
        'iconButton': AppSize.iconButton,
        'tabBar': AppSize.tabBar,
        'iconSm': AppSize.iconSm,
        'iconMd': AppSize.iconMd,
        'iconStroke': AppSize.iconStroke,
      };

      // Assert
      expect(actual, expected);
    });

    test('no interactive target falls below the 44px invariant', () {
      // Arrange
      const targets = <double>[
        AppSize.buttonPrimary,
        AppSize.buttonSecondary,
        AppSize.field,
        AppSize.listRowMin,
        AppSize.iconButton,
        AppSize.tabBar,
      ];

      // Act
      final smallest = targets.reduce((a, b) => a < b ? a : b);

      // Assert
      expect(smallest, greaterThanOrEqualTo(AppSize.minTapTarget));
    });
  });

  group('AppBorderWidth', () {
    test('separates sections at 2px and list rows at 1px', () {
      // Arrange
      const expected = (hairline: 1.0, rule: 2.0);

      // Act
      const actual = (
        hairline: AppBorderWidth.hairline,
        rule: AppBorderWidth.rule,
      );

      // Assert
      expect(actual, expected);
    });
  });

  group('AppLayout', () {
    test('carries the responsive figures from spec section 08', () {
      // Arrange
      const expected = <String, double>{
        'gutter': 16,
        'gutterLarge': 24,
        'contentMaxWidth': 520,
        'railWidth': 224,
        'largePhone': 480,
        'tablet': 768,
      };

      // Act
      const actual = <String, double>{
        'gutter': AppLayout.gutter,
        'gutterLarge': AppLayout.gutterLarge,
        'contentMaxWidth': AppLayout.contentMaxWidth,
        'railWidth': AppLayout.railWidth,
        'largePhone': AppBreakpoint.largePhone,
        'tablet': AppBreakpoint.tablet,
      };

      // Assert
      expect(actual, expected);
    });

    test('widens the gutter once past the large-phone breakpoint', () {
      // Arrange
      const phone = 390.0;
      const largePhone = AppBreakpoint.largePhone;

      // Act
      final gutters = (
        AppLayout.gutterFor(phone),
        AppLayout.gutterFor(largePhone),
      );

      // Assert
      expect(gutters, (AppLayout.gutter, AppLayout.gutterLarge));
    });

    test('never lets the gutter fall below the 16px invariant', () {
      // Arrange
      const widths = <double>[320, 390, 430, 480, 768, 1024];

      // Act
      final smallest = widths
          .map(AppLayout.gutterFor)
          .reduce((a, b) => a < b ? a : b);

      // Assert
      expect(smallest, greaterThanOrEqualTo(AppLayout.gutter));
    });
  });

  group('AppInsets', () {
    test('derives its padding from the spacing scale', () {
      // Arrange
      const expected = <String, EdgeInsets>{
        'screen': EdgeInsets.symmetric(horizontal: AppSpacing.x4),
        'card': EdgeInsets.all(AppSpacing.x3),
        'listRow': EdgeInsets.all(AppSpacing.x3),
        'section': EdgeInsets.symmetric(vertical: AppSpacing.x6),
      };

      // Act
      const actual = <String, EdgeInsets>{
        'screen': AppInsets.screen,
        'card': AppInsets.card,
        'listRow': AppInsets.listRow,
        'section': AppInsets.section,
      };

      // Assert
      expect(actual, expected);
    });
  });
}
