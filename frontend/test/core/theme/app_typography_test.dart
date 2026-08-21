import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/app_typography.dart';
import 'package:google_fonts/google_fonts.dart';

void main() {
  setUpAll(() {
    TestWidgetsFlutterBinding.ensureInitialized();
    GoogleFonts.config.allowRuntimeFetching = false;
  });

  group('AppFontWeight', () {
    test('exposes the three weights the design system ships', () {
      // Arrange
      const expected = <String, FontWeight>{
        'regular': FontWeight.w400,
        'semiBold': FontWeight.w600,
        'extraBold': FontWeight.w800,
      };

      // Act
      const actual = <String, FontWeight>{
        'regular': AppFontWeight.regular,
        'semiBold': AppFontWeight.semiBold,
        'extraBold': AppFontWeight.extraBold,
      };

      // Assert
      expect(actual, expected);
    });
  });

  group('AppFontSize', () {
    test('matches the mobile type scale in spec section 03', () {
      // Arrange
      const expected = <String, double>{
        'display': 28,
        'title': 22,
        'section': 17,
        'body': 15,
        'small': 13,
        'caption': 11,
        'overline': 10,
      };

      // Act
      const actual = <String, double>{
        'display': AppFontSize.display,
        'title': AppFontSize.title,
        'section': AppFontSize.section,
        'body': AppFontSize.body,
        'small': AppFontSize.small,
        'caption': AppFontSize.caption,
        'overline': AppFontSize.overline,
      };

      // Assert
      expect(actual, expected);
    });

    test('never drops below the 11px floor the spec sets', () {
      // Arrange
      const scale = <double>[
        AppFontSize.display,
        AppFontSize.title,
        AppFontSize.section,
        AppFontSize.body,
        AppFontSize.small,
        AppFontSize.caption,
      ];

      // Act
      final smallest = scale.reduce((a, b) => a < b ? a : b);

      // Assert
      expect(smallest, greaterThanOrEqualTo(11));
    });
  });

  group('AppTypography styles', () {
    test('carry the size, weight and line height of their spec row', () {
      // Arrange
      const expected = <String, (double, FontWeight, double)>{
        'display': (28, FontWeight.w800, 1.1),
        'title': (22, FontWeight.w800, 1.15),
        'section': (17, FontWeight.w800, 1.2),
        'body': (15, FontWeight.w400, 1.5),
        'bodyStrong': (15, FontWeight.w600, 1.5),
        'small': (13, FontWeight.w400, 1.45),
        'caption': (11, FontWeight.w400, 1.4),
        'overline': (10, FontWeight.w600, 1.0),
      };

      // Act
      final actual = <String, (double?, FontWeight?, double?)>{
        for (final e in <String, TextStyle>{
          'display': AppTypography.display,
          'title': AppTypography.title,
          'section': AppTypography.section,
          'body': AppTypography.body,
          'bodyStrong': AppTypography.bodyStrong,
          'small': AppTypography.small,
          'caption': AppTypography.caption,
          'overline': AppTypography.overline,
        }.entries)
          e.key: (e.value.fontSize, e.value.fontWeight, e.value.height),
      };

      // Assert
      expect(actual, expected);
    });

    test('resolve to the Archivo family', () {
      // Arrange
      final styles = <TextStyle>[
        AppTypography.display,
        AppTypography.body,
        AppTypography.overline,
      ];

      // Act
      final families = styles.map((s) => s.fontFamily).toSet();

      // Assert
      expect(families.every((f) => f != null && f.contains('Archivo')), isTrue);
    });

    test('tracks display tight and overline wide, per the spec samples', () {
      // Arrange
      const displayEm = -0.02;
      const overlineEm = 0.12;

      // Act
      final actual = <double?>[
        AppTypography.display.letterSpacing,
        AppTypography.overline.letterSpacing,
      ];

      // Assert
      expect(actual, <Matcher>[
        closeTo(displayEm * AppFontSize.display, 0.001),
        closeTo(overlineEm * AppFontSize.overline, 0.001),
      ]);
    });

    test('gives money styles tabular figures so columns align', () {
      // Arrange
      const expected = FontFeature.tabularFigures();

      // Act
      final actual = AppTypography.display.fontFeatures;

      // Assert
      expect(actual, contains(expected));
    });

    test('tabular() adds tabular figures to any style', () {
      // Arrange
      final plain = AppTypography.bodyStrong;

      // Act
      final actual = AppTypography.tabular(plain);

      // Assert
      expect(actual.fontFeatures, contains(const FontFeature.tabularFigures()));
    });
  });

  group('AppTypography.textTheme', () {
    test('maps every named role onto a Material slot', () {
      // Arrange
      const color = Color(0xFF201E1D);

      // Act
      final theme = AppTypography.textTheme(color);

      // Assert
      expect(
        <String, double?>{
          'displaySmall': theme.displaySmall?.fontSize,
          'headlineSmall': theme.headlineSmall?.fontSize,
          'titleMedium': theme.titleMedium?.fontSize,
          'titleSmall': theme.titleSmall?.fontSize,
          'bodyLarge': theme.bodyLarge?.fontSize,
          'bodyMedium': theme.bodyMedium?.fontSize,
          'bodySmall': theme.bodySmall?.fontSize,
          'labelLarge': theme.labelLarge?.fontSize,
          'labelSmall': theme.labelSmall?.fontSize,
        },
        <String, double>{
          'displaySmall': AppFontSize.display,
          'headlineSmall': AppFontSize.title,
          'titleMedium': AppFontSize.section,
          'titleSmall': AppFontSize.body,
          'bodyLarge': AppFontSize.body,
          'bodyMedium': AppFontSize.small,
          'bodySmall': AppFontSize.caption,
          'labelLarge': AppFontSize.body,
          'labelSmall': AppFontSize.overline,
        },
      );
    });

    test('paints every slot with the supplied ink colour', () {
      // Arrange
      const color = Color(0xFFF3F2F2);

      // Act
      final theme = AppTypography.textTheme(color);

      // Assert
      expect([
        theme.displaySmall?.color,
        theme.headlineSmall?.color,
        theme.bodyLarge?.color,
        theme.labelSmall?.color,
      ], everyElement(color));
    });
  });
}
