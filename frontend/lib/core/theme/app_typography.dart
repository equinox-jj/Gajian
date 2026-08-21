import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

/// The three Archivo weights the design system ships.
abstract final class AppFontWeight {
  const AppFontWeight._();

  static const FontWeight regular = FontWeight.w400;
  static const FontWeight semiBold = FontWeight.w600;
  static const FontWeight extraBold = FontWeight.w800;
}

/// The mobile type scale from spec section 03, re-cut for a 390pt viewport.
///
/// Fixed: density changes spacing, not sizes. Nothing goes below 11px and no
/// body copy below 13px.
abstract final class AppFontSize {
  const AppFontSize._();

  static const double display = 28;
  static const double title = 22;
  static const double section = 17;
  static const double body = 15;
  static const double small = 13;
  static const double caption = 11;
  static const double overline = 10;
}

/// Named text styles, one per row of the spec's type table.
///
/// Letter spacing is stored in the spec's em units and multiplied by the size,
/// because Flutter's [TextStyle.letterSpacing] is in logical pixels.
abstract final class AppTypography {
  const AppTypography._();

  static const String fontFamily = 'Archivo';

  static const double _displayTracking = -0.02;
  static const double _overlineTracking = 0.12;

  static const List<FontFeature> _tabular = <FontFeature>[
    FontFeature.tabularFigures(),
  ];

  /// Net pay and balance figures. Tabular by default — these are always money.
  static TextStyle get display => _archivo(
    size: AppFontSize.display,
    weight: AppFontWeight.extraBold,
    height: 1.1,
    letterSpacing: _displayTracking * AppFontSize.display,
    features: _tabular,
  );

  /// Screen titles.
  static TextStyle get title => _archivo(
    size: AppFontSize.title,
    weight: AppFontWeight.extraBold,
    height: 1.15,
  );

  /// Card titles and list headers.
  static TextStyle get section => _archivo(
    size: AppFontSize.section,
    weight: AppFontWeight.extraBold,
    height: 1.2,
  );

  /// Primary content and list rows.
  static TextStyle get body =>
      _archivo(size: AppFontSize.body, weight: AppFontWeight.regular);

  /// The value half of a row.
  static TextStyle get bodyStrong =>
      _archivo(size: AppFontSize.body, weight: AppFontWeight.semiBold);

  /// Secondary and helper text.
  static TextStyle get small => _archivo(
    size: AppFontSize.small,
    weight: AppFontWeight.regular,
    height: 1.45,
  );

  /// Meta and timestamps.
  static TextStyle get caption => _archivo(
    size: AppFontSize.caption,
    weight: AppFontWeight.regular,
    height: 1.4,
  );

  /// Group labels and kickers. Rendered upper-case by the caller — Flutter has
  /// no `text-transform`, so the string itself carries the casing.
  static TextStyle get overline => _archivo(
    size: AppFontSize.overline,
    weight: AppFontWeight.semiBold,
    height: 1,
    letterSpacing: _overlineTracking * AppFontSize.overline,
  );

  /// Button labels: heading face, heading weight, body size.
  static TextStyle get button =>
      _archivo(size: AppFontSize.body, weight: AppFontWeight.extraBold);

  /// Returns [style] with tabular figures, so currency columns line up.
  static TextStyle tabular(TextStyle style) =>
      style.copyWith(fontFeatures: _tabular);

  /// The scale mapped onto Material's slots and inked with [color].
  ///
  /// Slot choices are deliberate: [bodyStrong] takes `titleSmall` and [button]
  /// takes `labelLarge`, which is what Material reads for button labels.
  static TextTheme textTheme(Color color) => TextTheme(
    displaySmall: display,
    headlineSmall: title,
    titleMedium: section,
    titleSmall: bodyStrong,
    bodyLarge: body,
    bodyMedium: small,
    bodySmall: caption,
    labelLarge: button,
    labelSmall: overline,
  ).apply(bodyColor: color, displayColor: color);

  static TextStyle _archivo({
    required double size,
    required FontWeight weight,
    double height = 1.5,
    double? letterSpacing,
    List<FontFeature>? features,
  }) => GoogleFonts.archivo(
    fontSize: size,
    fontWeight: weight,
    height: height,
    letterSpacing: letterSpacing,
    fontFeatures: features,
  );
}
