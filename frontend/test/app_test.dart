import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gajian/core/theme/theme.dart';
import 'package:gajian/main.dart';
import 'package:google_fonts/google_fonts.dart';

void main() {
  setUpAll(() {
    GoogleFonts.config.allowRuntimeFetching = false;
  });

  group('GajianApp', () {
    testWidgets('hands both themes to MaterialApp and follows the device', (
      tester,
    ) async {
      // Arrange
      const app = GajianApp();

      // Act
      await tester.pumpWidget(app);

      // Assert
      final materialApp = tester.widget<MaterialApp>(find.byType(MaterialApp));
      expect(
        (
          materialApp.theme?.extension<AppPalette>(),
          materialApp.darkTheme?.extension<AppPalette>(),
          materialApp.themeMode,
        ),
        (AppPalette.light, AppPalette.dark, ThemeMode.system),
      );
    });

    testWidgets('builds without exception under the light theme', (
      tester,
    ) async {
      // Arrange
      tester.platformDispatcher.platformBrightnessTestValue = Brightness.light;
      addTearDown(tester.platformDispatcher.clearPlatformBrightnessTestValue);

      // Act
      await tester.pumpWidget(const GajianApp());

      // Assert
      expect(tester.takeException(), isNull);
    });

    testWidgets('builds without exception under the dark theme', (
      tester,
    ) async {
      // Arrange
      tester.platformDispatcher.platformBrightnessTestValue = Brightness.dark;
      addTearDown(tester.platformDispatcher.clearPlatformBrightnessTestValue);

      // Act
      await tester.pumpWidget(const GajianApp());

      // Assert
      expect(tester.takeException(), isNull);
    });
  });
}
