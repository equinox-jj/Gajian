import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/app_palette.dart';
import 'package:gajian/core/theme/app_spacing.dart';
import 'package:gajian/core/theme/app_theme.dart';
import 'package:gajian/core/theme/app_typography.dart';
import 'package:google_fonts/google_fonts.dart';

/// Pulls the corner radius out of the shapes the theme is allowed to use.
/// Returns null for shapes that carry no radius at all (e.g. [Border]).
BorderRadius? radiusOf(ShapeBorder? shape) => switch (shape) {
  RoundedRectangleBorder(:final borderRadius) => borderRadius.resolve(
    TextDirection.ltr,
  ),
  ContinuousRectangleBorder(:final borderRadius) => borderRadius.resolve(
    TextDirection.ltr,
  ),
  BeveledRectangleBorder(:final borderRadius) => borderRadius.resolve(
    TextDirection.ltr,
  ),
  _ => null,
};

List<ShapeBorder?> shapesOf(ThemeData t) => <ShapeBorder?>[
  t.cardTheme.shape,
  t.dialogTheme.shape,
  t.bottomSheetTheme.shape,
  t.popupMenuTheme.shape,
  t.snackBarTheme.shape,
  t.chipTheme.shape,
  t.appBarTheme.shape,
  t.elevatedButtonTheme.style?.shape?.resolve(<WidgetState>{}),
  t.filledButtonTheme.style?.shape?.resolve(<WidgetState>{}),
  t.outlinedButtonTheme.style?.shape?.resolve(<WidgetState>{}),
  t.textButtonTheme.style?.shape?.resolve(<WidgetState>{}),
  t.iconButtonTheme.style?.shape?.resolve(<WidgetState>{}),
];

List<InputBorder?> inputBordersOf(ThemeData t) => <InputBorder?>[
  t.inputDecorationTheme.border,
  t.inputDecorationTheme.enabledBorder,
  t.inputDecorationTheme.focusedBorder,
  t.inputDecorationTheme.errorBorder,
  t.inputDecorationTheme.focusedErrorBorder,
  t.inputDecorationTheme.disabledBorder,
];

void main() {
  setUpAll(() {
    TestWidgetsFlutterBinding.ensureInitialized();
    GoogleFonts.config.allowRuntimeFetching = false;
  });

  group('AppTheme brightness', () {
    test('light and dark declare their brightness', () {
      // Arrange
      const expected = (Brightness.light, Brightness.dark);

      // Act
      final actual = (AppTheme.light.brightness, AppTheme.dark.brightness);

      // Assert
      expect(actual, expected);
    });
  });

  group('AppTheme palette wiring', () {
    test('both themes carry an AppPalette extension', () {
      // Arrange
      const expected = (AppPalette.light, AppPalette.dark);

      // Act
      final actual = (
        AppTheme.light.extension<AppPalette>(),
        AppTheme.dark.extension<AppPalette>(),
      );

      // Assert
      expect(actual, expected);
    });

    test('grounds the scaffold on the palette background', () {
      // Arrange
      const expected = (AppPalette.light, AppPalette.dark);

      // Act
      final actual = (
        AppTheme.light.scaffoldBackgroundColor,
        AppTheme.dark.scaffoldBackgroundColor,
      );

      // Assert
      expect(actual, (expected.$1.bg, expected.$2.bg));
    });

    test('maps the accent onto the colour scheme primary', () {
      // Arrange
      const expected = (AppPalette.light, AppPalette.dark);

      // Act
      final actual = (
        AppTheme.light.colorScheme.primary,
        AppTheme.dark.colorScheme.primary,
      );

      // Assert
      expect(actual, (expected.$1.accent, expected.$2.accent));
    });

    test('maps the status rejection colour onto the scheme error role', () {
      // Arrange
      final expected = AppPalette.light.rejectedFg;

      // Act
      final actual = AppTheme.light.colorScheme.error;

      // Assert
      expect(actual, expected);
    });
  });

  group('AppTheme radius', () {
    test('every shape the light theme sets is square', () {
      // Arrange
      const expected = BorderRadius.zero;

      // Act
      final radii = shapesOf(AppTheme.light)
          .map(radiusOf)
          .whereType<BorderRadius>()
          .toSet();

      // Assert
      expect(radii, anyOf(isEmpty, {expected}));
    });

    test('every shape the dark theme sets is square', () {
      // Arrange
      const expected = BorderRadius.zero;

      // Act
      final radii = shapesOf(AppTheme.dark)
          .map(radiusOf)
          .whereType<BorderRadius>()
          .toSet();

      // Assert
      expect(radii, anyOf(isEmpty, {expected}));
    });

    test('no themed shape is a stadium or a circle', () {
      // Arrange
      final shapes = [...shapesOf(AppTheme.light), ...shapesOf(AppTheme.dark)];

      // Act
      final rounded = shapes
          .where((s) => s is StadiumBorder || s is CircleBorder)
          .toList();

      // Assert
      expect(rounded, isEmpty);
    });

    test('every input border is square', () {
      // Arrange
      const expected = BorderRadius.zero;

      // Act
      final radii = inputBordersOf(AppTheme.light)
          .whereType<InputBorder>()
          .map((b) => b is OutlineInputBorder ? b.borderRadius : null)
          .whereType<BorderRadius>()
          .toSet();

      // Assert
      expect(radii, {expected});
    });
  });

  group('AppTheme targets', () {
    test('the primary button stands at its spec height', () {
      // Arrange
      const expected = AppSize.buttonPrimary;

      // Act
      final actual = AppTheme.light.filledButtonTheme.style?.minimumSize
          ?.resolve(<WidgetState>{})
          ?.height;

      // Assert
      expect(actual, expected);
    });

    test('the secondary button stands at its spec height', () {
      // Arrange
      const expected = AppSize.buttonSecondary;

      // Act
      final actual = AppTheme.light.outlinedButtonTheme.style?.minimumSize
          ?.resolve(<WidgetState>{})
          ?.height;

      // Assert
      expect(actual, expected);
    });

    test('the field stands at its spec height', () {
      // Arrange
      const expected = AppSize.field;

      // Act
      final actual = AppTheme.light.inputDecorationTheme.constraints?.minHeight;

      // Assert
      expect(actual, expected);
    });

    test('the navigation bar stands at its spec height', () {
      // Arrange
      const expected = AppSize.tabBar;

      // Act
      final actual = AppTheme.light.navigationBarTheme.height;

      // Assert
      expect(actual, expected);
    });
  });

  group('AppTheme rules', () {
    test('divides list rows with a hairline in the palette divider colour', () {
      // Arrange
      const expected = (AppBorderWidth.hairline, AppPalette.light);

      // Act
      final actual = (
        AppTheme.light.dividerTheme.thickness,
        AppTheme.light.dividerTheme.color,
      );

      // Assert
      expect(actual, (expected.$1, expected.$2.divider));
    });

    test('kills the M3 surface tint so no accent leaks into the greys', () {
      // Arrange
      const expected = (Colors.transparent, Colors.transparent);

      // Act
      final actual = (
        AppTheme.light.colorScheme.surfaceTint,
        AppTheme.dark.colorScheme.surfaceTint,
      );

      // Assert
      expect(actual, expected);
    });

    test('flattens elevation on the app bar', () {
      // Arrange
      const expected = (0.0, 0.0);

      // Act
      final actual = (
        AppTheme.light.appBarTheme.elevation,
        AppTheme.light.appBarTheme.scrolledUnderElevation,
      );

      // Assert
      expect(actual, expected);
    });
  });

  group('AppTheme typography', () {
    test('inks the text theme with the palette text colour', () {
      // Arrange
      const expected = (AppPalette.light, AppPalette.dark);

      // Act
      final actual = (
        AppTheme.light.textTheme.bodyLarge?.color,
        AppTheme.dark.textTheme.bodyLarge?.color,
      );

      // Assert
      expect(actual, (expected.$1.text, expected.$2.text));
    });

    test('uses the spec body size for the default body slot', () {
      // Arrange
      const expected = AppFontSize.body;

      // Act
      final actual = AppTheme.light.textTheme.bodyLarge?.fontSize;

      // Assert
      expect(actual, expected);
    });
  });

  group('AppTheme in a widget tree', () {
    testWidgets('a Scaffold paints the palette background', (tester) async {
      // Arrange
      final app = MaterialApp(
        theme: AppTheme.light,
        home: const Scaffold(body: SizedBox.shrink()),
      );

      // Act
      await tester.pumpWidget(app);

      // Assert
      final scaffold = tester.widget<Material>(
        find
            .descendant(
              of: find.byType(Scaffold),
              matching: find.byType(Material),
            )
            .first,
      );
      expect(scaffold.color, AppPalette.light.bg);
    });

    testWidgets('a FilledButton renders square at the spec height', (
      tester,
    ) async {
      // Arrange
      final app = MaterialApp(
        theme: AppTheme.light,
        home: Scaffold(
          body: FilledButton(onPressed: () {}, child: const Text('Check in')),
        ),
      );

      // Act
      await tester.pumpWidget(app);

      // Assert
      final size = tester.getSize(find.byType(FilledButton));
      expect(size.height, AppSize.buttonPrimary);
    });
  });
}
