import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_palette.dart';
import 'app_shadows.dart';
import 'app_spacing.dart';
import 'app_typography.dart';

/// Assembles the design system into the two [ThemeData]s the app runs on.
///
/// Component themes are configured deliberately, not for completeness: without
/// them Material's defaults reintroduce rounded corners, surface tints and
/// wrong control heights, each of which contradicts the spec.
abstract final class AppTheme {
  const AppTheme._();

  static ThemeData get light => _build(AppPalette.light, Brightness.light);

  static ThemeData get dark => _build(AppPalette.dark, Brightness.dark);

  static ThemeData _build(AppPalette p, Brightness brightness) {
    final ColorScheme scheme = _scheme(p, brightness);
    final TextTheme text = AppTypography.textTheme(p.text);

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: scheme,
      extensions: <ThemeExtension<dynamic>>[p],
      scaffoldBackgroundColor: p.bg,
      canvasColor: p.bg,
      shadowColor: AppShadows.ink,
      textTheme: text,
      splashFactory: InkRipple.splashFactory,

      iconTheme: IconThemeData(color: p.text, size: AppSize.iconMd),
      primaryIconTheme: IconThemeData(color: p.onAccent, size: AppSize.iconMd),

      dividerTheme: DividerThemeData(
        color: p.divider,
        thickness: AppBorderWidth.hairline,
        space: AppBorderWidth.hairline,
      ),

      appBarTheme: AppBarTheme(
        backgroundColor: p.bg,
        foregroundColor: p.text,
        elevation: 0,
        scrolledUnderElevation: 0,
        centerTitle: false,
        toolbarHeight: AppSize.appBar,
        titleTextStyle: text.titleMedium,
        iconTheme: IconThemeData(color: p.text, size: AppSize.iconMd),
        shape: Border(
          bottom: BorderSide(color: p.divider, width: AppBorderWidth.rule),
        ),
      ),

      cardTheme: CardThemeData(
        color: p.surface,
        shadowColor: AppShadows.ink,
        elevation: 0,
        margin: EdgeInsets.zero,
        shape: AppShape.rectangle,
      ),

      // Dialogs and sheets are the only things that float over content, so
      // they are the only things that carry elevation. The exact CSS shadow is
      // AppShadows.lg, for surfaces the app paints itself.
      dialogTheme: DialogThemeData(
        backgroundColor: p.surface,
        shadowColor: AppShadows.ink,
        elevation: 12,
        shape: AppShape.rectangle,
        titleTextStyle: text.headlineSmall,
        contentTextStyle: text.bodyLarge,
      ),

      bottomSheetTheme: BottomSheetThemeData(
        backgroundColor: p.surface,
        shadowColor: AppShadows.ink,
        modalBackgroundColor: p.surface,
        elevation: 12,
        modalElevation: 12,
        shape: AppShape.rectangle,
        showDragHandle: false,
      ),

      popupMenuTheme: PopupMenuThemeData(
        color: p.surface,
        elevation: 3,
        shape: AppShape.rectangle,
        textStyle: text.bodyLarge,
      ),

      snackBarTheme: SnackBarThemeData(
        backgroundColor: scheme.inverseSurface,
        contentTextStyle: text.bodyMedium?.copyWith(
          color: scheme.onInverseSurface,
        ),
        actionTextColor: p.accent,
        behavior: SnackBarBehavior.floating,
        elevation: 6,
        shape: AppShape.rectangle,
      ),

      chipTheme: ChipThemeData(
        backgroundColor: p.mutedBg,
        labelStyle: text.bodySmall?.copyWith(
          color: p.mutedFg,
          fontWeight: AppFontWeight.semiBold,
        ),
        side: BorderSide.none,
        shape: AppShape.rectangle,
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.x2,
          vertical: AppSpacing.x1,
        ),
        labelPadding: EdgeInsets.zero,
      ),

      inputDecorationTheme: _inputTheme(p, text),
      filledButtonTheme: FilledButtonThemeData(style: _primaryStyle(p, text)),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: _primaryStyle(p, text),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: _secondaryStyle(p, text),
      ),
      textButtonTheme: TextButtonThemeData(style: _ghostStyle(p, text)),
      iconButtonTheme: IconButtonThemeData(style: _iconStyle(p)),

      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: p.bg,
        indicatorColor: Colors.transparent,
        indicatorShape: AppShape.rectangle,
        height: AppSize.tabBar,
        elevation: 0,
        labelBehavior: NavigationDestinationLabelBehavior.alwaysShow,
        iconTheme: WidgetStateProperty.resolveWith(
          (states) => IconThemeData(
            size: AppSize.iconSm,
            color: states.contains(WidgetState.selected)
                ? p.accent
                : p.textMuted,
          ),
        ),
        labelTextStyle: WidgetStateProperty.resolveWith(
          (states) => text.labelSmall?.copyWith(
            color: states.contains(WidgetState.selected)
                ? p.accent
                : p.textMuted,
          ),
        ),
      ),

      navigationRailTheme: NavigationRailThemeData(
        backgroundColor: p.bg,
        indicatorColor: Colors.transparent,
        indicatorShape: AppShape.rectangle,
        elevation: 0,
        minWidth: AppSize.iconButton,
        selectedIconTheme: IconThemeData(color: p.accent, size: AppSize.iconSm),
        unselectedIconTheme: IconThemeData(
          color: p.textMuted,
          size: AppSize.iconSm,
        ),
        selectedLabelTextStyle: text.bodyMedium?.copyWith(color: p.accent),
        unselectedLabelTextStyle: text.bodyMedium?.copyWith(color: p.textMuted),
      ),

      listTileTheme: ListTileThemeData(
        minVerticalPadding: AppSpacing.x3,
        contentPadding: AppInsets.listRow,
        shape: AppShape.rectangle,
        titleTextStyle: text.titleSmall,
        subtitleTextStyle: text.bodySmall?.copyWith(color: p.textMuted),
        iconColor: p.text,
      ),

      progressIndicatorTheme: ProgressIndicatorThemeData(
        color: p.accent,
        linearTrackColor: p.skeleton,
        circularTrackColor: p.skeleton,
      ),

      checkboxTheme: CheckboxThemeData(
        shape: AppShape.rectangle,
        side: BorderSide(color: p.divider, width: 1.5),
        fillColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.selected)
              ? p.accent
              : Colors.transparent,
        ),
        checkColor: WidgetStatePropertyAll<Color>(p.onAccent),
      ),

      radioTheme: RadioThemeData(
        fillColor: WidgetStateProperty.resolveWith(
          (states) =>
              states.contains(WidgetState.selected) ? p.accent : p.divider,
        ),
      ),

      switchTheme: SwitchThemeData(
        thumbColor: WidgetStateProperty.resolveWith(
          (states) =>
              states.contains(WidgetState.selected) ? p.onAccent : p.surfaceAlt,
        ),
        trackColor: WidgetStateProperty.resolveWith(
          (states) =>
              states.contains(WidgetState.selected) ? p.accent : p.skeleton,
        ),
        trackOutlineColor: WidgetStatePropertyAll<Color>(p.divider),
      ),

      tabBarTheme: TabBarThemeData(
        labelColor: p.accent,
        unselectedLabelColor: p.textMuted,
        labelStyle: text.titleSmall,
        unselectedLabelStyle: text.bodyLarge,
        dividerColor: p.divider,
        indicatorSize: TabBarIndicatorSize.tab,
        indicator: UnderlineTabIndicator(
          borderSide: BorderSide(color: p.accent, width: AppBorderWidth.rule),
        ),
      ),
    );
  }

  static ColorScheme _scheme(AppPalette p, Brightness brightness) =>
      ColorScheme(
        brightness: brightness,
        primary: p.accent,
        onPrimary: p.onAccent,
        primaryContainer: p.accent,
        onPrimaryContainer: p.onAccent,
        secondary: p.accentSecondary,
        onSecondary: p.onAccent,
        error: p.rejectedFg,
        onError: p.onAccent,
        errorContainer: p.rejectedBg,
        onErrorContainer: p.rejectedFg,
        // M3 tints every raised surface with the primary by default, which
        // would push red through a palette that is deliberately
        // mono-with-one-accent. Killing it here covers every component that
        // resolves `surfaceTintColor` from the scheme.
        surfaceTint: Colors.transparent,
        surface: p.bg,
        onSurface: p.text,
        onSurfaceVariant: p.textMuted,
        surfaceContainerLowest: p.bg,
        surfaceContainerLow: p.bg,
        surfaceContainer: p.surface,
        surfaceContainerHigh: p.surfaceAlt,
        surfaceContainerHighest: p.surfaceAlt,
        outline: p.divider,
        outlineVariant: p.divider,
        shadow: AppShadows.ink,
        inverseSurface: p.text,
        onInverseSurface: p.bg,
        inversePrimary: p.accent,
      );

  static InputDecorationTheme _inputTheme(AppPalette p, TextTheme text) {
    OutlineInputBorder border(Color color, double width) => OutlineInputBorder(
      borderRadius: AppRadius.none,
      borderSide: BorderSide(color: color, width: width),
    );

    return InputDecorationTheme(
      filled: true,
      fillColor: p.surface,
      constraints: const BoxConstraints(minHeight: AppSize.field),
      contentPadding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.x3,
        vertical: AppSpacing.x3,
      ),
      // The design system's label is 12px, which is off the mobile type scale;
      // the scale is fixed, so the nearest step down (13) is used instead.
      labelStyle: text.bodyMedium?.copyWith(color: p.textLabel),
      floatingLabelStyle: text.bodyMedium?.copyWith(color: p.accent),
      hintStyle: text.bodyLarge?.copyWith(color: p.textMuted),
      helperStyle: text.bodySmall?.copyWith(color: p.textMuted),
      errorStyle: text.bodySmall?.copyWith(color: p.rejectedFg),
      border: border(p.divider, AppBorderWidth.hairline),
      enabledBorder: border(p.divider, AppBorderWidth.hairline),
      disabledBorder: border(p.divider, AppBorderWidth.hairline),
      focusedBorder: border(p.accent, AppBorderWidth.rule),
      errorBorder: border(p.accent, AppBorderWidth.rule),
      focusedErrorBorder: border(p.accent, AppBorderWidth.rule),
    );
  }

  /// Accent block, ink-on-accent label. Hover and pressed step down the accent
  /// ramp, which is theme-independent — the same steps in light and dark.
  static ButtonStyle _primaryStyle(AppPalette p, TextTheme text) =>
      _base(text).copyWith(
        minimumSize: const WidgetStatePropertyAll<Size>(
          Size.fromHeight(AppSize.buttonPrimary),
        ),
        backgroundColor: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.disabled)) {
            return p.accent.withValues(alpha: _disabledOpacity);
          }
          if (states.contains(WidgetState.pressed)) return AppColors.accent700;
          if (states.contains(WidgetState.hovered)) return AppColors.accent600;
          return p.accent;
        }),
        foregroundColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.disabled)
              ? p.onAccent.withValues(alpha: _disabledOpacity)
              : p.onAccent,
        ),
        overlayColor: const WidgetStatePropertyAll<Color>(Colors.transparent),
      );

  /// Outlined on the ground, ink label, tinted on interaction.
  static ButtonStyle _secondaryStyle(AppPalette p, TextTheme text) =>
      _base(text).copyWith(
        minimumSize: const WidgetStatePropertyAll<Size>(
          Size.fromHeight(AppSize.buttonSecondary),
        ),
        foregroundColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.disabled)
              ? p.text.withValues(alpha: _disabledOpacity)
              : p.text,
        ),
        side: WidgetStateProperty.resolveWith(
          (states) => BorderSide(
            color: states.contains(WidgetState.disabled)
                ? p.divider.withValues(alpha: _disabledOpacity)
                : p.divider,
            width: AppBorderWidth.hairline,
          ),
        ),
        overlayColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.pressed)
              ? p.text.withValues(alpha: 0.14)
              : p.text.withValues(alpha: 0.07),
        ),
      );

  /// Accent label, no chrome.
  static ButtonStyle _ghostStyle(AppPalette p, TextTheme text) =>
      _base(text).copyWith(
        minimumSize: const WidgetStatePropertyAll<Size>(
          Size.fromHeight(AppSize.iconButton),
        ),
        padding: const WidgetStatePropertyAll<EdgeInsets>(
          EdgeInsets.symmetric(horizontal: AppSpacing.x1),
        ),
        foregroundColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.disabled)
              ? p.accent.withValues(alpha: _disabledOpacity)
              : p.accent,
        ),
        overlayColor: WidgetStateProperty.resolveWith(
          (states) => states.contains(WidgetState.pressed)
              ? p.accent.withValues(alpha: 0.18)
              : p.accent.withValues(alpha: 0.10),
        ),
      );

  static ButtonStyle _iconStyle(AppPalette p) => ButtonStyle(
    minimumSize: const WidgetStatePropertyAll<Size>(
      Size.square(AppSize.iconButton),
    ),
    fixedSize: const WidgetStatePropertyAll<Size>(
      Size.square(AppSize.iconButton),
    ),
    padding: const WidgetStatePropertyAll<EdgeInsets>(EdgeInsets.zero),
    shape: const WidgetStatePropertyAll<OutlinedBorder>(AppShape.rectangle),
    iconSize: const WidgetStatePropertyAll<double>(AppSize.iconSm),
    foregroundColor: WidgetStatePropertyAll<Color>(p.text),
    iconColor: WidgetStatePropertyAll<Color>(p.text),
    overlayColor: WidgetStatePropertyAll<Color>(p.text.withValues(alpha: 0.07)),
  );

  /// Shared across every button variant: square, flat, flush-left label at the
  /// full width it is given.
  static ButtonStyle _base(TextTheme text) => ButtonStyle(
    shape: const WidgetStatePropertyAll<OutlinedBorder>(AppShape.rectangle),
    padding: const WidgetStatePropertyAll<EdgeInsets>(
      EdgeInsets.symmetric(horizontal: AppSpacing.x4),
    ),
    textStyle: WidgetStatePropertyAll<TextStyle?>(text.labelLarge),
    elevation: const WidgetStatePropertyAll<double>(0),
    shadowColor: const WidgetStatePropertyAll<Color>(Colors.transparent),
    surfaceTintColor: const WidgetStatePropertyAll<Color>(Colors.transparent),
    alignment: Alignment.centerLeft,
    tapTargetSize: MaterialTapTargetSize.shrinkWrap,
    visualDensity: VisualDensity.standard,
  );

  /// `.btn:disabled { opacity: 0.45 }`.
  static const double _disabledOpacity = 0.45;
}
