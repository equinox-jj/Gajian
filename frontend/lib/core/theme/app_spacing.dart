import 'package:flutter/material.dart';

/// The 4px-base spacing scale (`--space-*`).
///
/// Step names mirror the CSS custom properties, so `--space-6` is [x6] and the
/// mapping back to the design system stays one-to-one. Density changes spacing,
/// never type size.
abstract final class AppSpacing {
  const AppSpacing._();

  /// Gap between an icon and its label.
  static const double x1 = 4;

  /// Gap inside a row.
  static const double x2 = 8;

  /// Card padding.
  static const double x3 = 12;

  /// Screen gutter.
  static const double x4 = 16;

  /// Between blocks.
  static const double x6 = 24;

  /// Section break.
  static const double x8 = 32;
}

/// Corner radius.
///
/// The system is square — `--radius-sm`, `--radius-md` and `--radius-lg` are
/// all `0px`, a deliberate modernist choice rather than an unset default. One
/// constant, not three, so no call site can quietly round one level.
abstract final class AppRadius {
  const AppRadius._();

  static const BorderRadius none = BorderRadius.zero;
}

/// Shapes built on [AppRadius].
abstract final class AppShape {
  const AppShape._();

  static const RoundedRectangleBorder rectangle = RoundedRectangleBorder(
    borderRadius: AppRadius.none,
  );
}

/// Touch targets and icon metrics from spec section 04.
abstract final class AppSize {
  const AppSize._();

  static const double buttonPrimary = 52;
  static const double buttonSecondary = 48;
  static const double field = 48;
  static const double listRowMin = 64;
  static const double iconButton = 44;
  static const double tabBar = 56;
  static const double appBar = 56;

  static const double iconSm = 20;
  static const double iconMd = 24;
  static const double iconStroke = 2;

  /// Floor for anything interactive; targets never shrink below it.
  static const double minTapTarget = 44;
}

/// Rule weights. Sections are separated by a [rule], rows inside a list by a
/// [hairline].
abstract final class AppBorderWidth {
  const AppBorderWidth._();

  static const double hairline = 1;
  static const double rule = 2;
}

/// Viewport widths where the layout changes, from spec section 08.
abstract final class AppBreakpoint {
  const AppBreakpoint._();

  /// Large phone / fold: wider gutter, capped content, 2-up stat blocks.
  static const double largePhone = 480;

  /// Tablet: the tab bar becomes a left rail and list/detail sit side by side.
  static const double tablet = 768;
}

/// Page-level measurements.
abstract final class AppLayout {
  const AppLayout._();

  static const double gutter = 16;
  static const double gutterLarge = 24;
  static const double contentMaxWidth = 520;
  static const double railWidth = 224;

  /// Horizontal gutter for a viewport [width]. Never returns less than
  /// [gutter] — that floor is an invariant of the responsive spec.
  static double gutterFor(double width) =>
      width >= AppBreakpoint.largePhone ? gutterLarge : gutter;
}

/// Padding built from [AppSpacing], for the few shapes that recur across
/// features. Anything one-off should compose [AppSpacing] at the call site.
abstract final class AppInsets {
  const AppInsets._();

  static const EdgeInsets screen = EdgeInsets.symmetric(
    horizontal: AppSpacing.x4,
  );
  static const EdgeInsets card = EdgeInsets.all(AppSpacing.x3);
  static const EdgeInsets listRow = EdgeInsets.all(AppSpacing.x3);
  static const EdgeInsets section = EdgeInsets.symmetric(
    vertical: AppSpacing.x6,
  );
}
