import 'package:flutter/painting.dart';

/// Raw colour values of the Modernist design system.
///
/// Transcribed from `docs/GajianDesign/_ds/modernist-*/styles.css` (`:root`)
/// and the prototype's runtime token map in `GajianMobileApp.dc.html`, which
/// is the only place the dark ground is defined.
///
/// These are literals, not roles. Nothing outside [AppPalette] should read the
/// `light*` / `dark*` groups directly — widgets ask the palette for a role so
/// the value follows the active theme. The ramps are theme-independent and are
/// fine to use anywhere a specific tint step is called for.
abstract final class AppColors {
  const AppColors._();

  // ── Neutral ramp ────────────────────────────────────────────────────────
  // Tinted fills 100–300, base 500, text-on-tint 700–900.
  static const Color neutral100 = Color(0xFFF8F4F4);
  static const Color neutral200 = Color(0xFFEAE7E7);
  static const Color neutral300 = Color(0xFFD7D3D3);
  static const Color neutral400 = Color(0xFFBAB6B6);
  static const Color neutral500 = Color(0xFF9B9797);
  static const Color neutral600 = Color(0xFF7D7979);
  static const Color neutral700 = Color(0xFF605D5D);
  static const Color neutral800 = Color(0xFF444141);
  static const Color neutral900 = Color(0xFF2D2B2B);

  // ── Accent ramp ─────────────────────────────────────────────────────────
  static const Color accent100 = Color(0xFFFFF2EF);
  static const Color accent200 = Color(0xFFFFE0D9);
  static const Color accent300 = Color(0xFFFFC4B8);
  static const Color accent400 = Color(0xFFFF9783);
  static const Color accent500 = Color(0xFFFF563C);
  static const Color accent600 = Color(0xFFDD2B0F);
  static const Color accent700 = Color(0xFFAE1800);
  static const Color accent800 = Color(0xFF7C1405);
  static const Color accent900 = Color(0xFF4D170E);

  // ── Secondary accent ramp (`--color-accent-2`) ──────────────────────────
  static const Color accentSecondary = Color(0xFFE15B47);
  static const Color accentSecondary100 = Color(0xFFFFF2EF);
  static const Color accentSecondary200 = Color(0xFFFFE0DA);
  static const Color accentSecondary300 = Color(0xFFFFC4B9);
  static const Color accentSecondary400 = Color(0xFFFF9784);
  static const Color accentSecondary500 = Color(0xFFEF6853);
  static const Color accentSecondary600 = Color(0xFFC94B39);
  static const Color accentSecondary700 = Color(0xFF9E3526);
  static const Color accentSecondary800 = Color(0xFF71261B);
  static const Color accentSecondary900 = Color(0xFF471D16);

  // ── Light ground ────────────────────────────────────────────────────────
  static const Color lightBg = Color(0xFFF3F2F2);
  static const Color lightSurface = Color(0xFFEAE9E9);
  static const Color lightSurfaceAlt = Color(0xFFDEDCDC);
  static const Color lightText = Color(0xFF201E1D);
  static const Color lightAccent = Color(0xFFEC3013);
  static const Color lightOnAccent = Color(0xFFF3F2F2);
  static const Color lightSkeleton = Color(0xFFD7D3D3);
  static const Color lightSkeletonAlt = Color(0xFFE4E1E1);

  /// Alpha the light theme applies to [lightText] to draw a divider.
  static const double lightDividerAlpha = 0.40;

  // ── Dark ground ─────────────────────────────────────────────────────────
  // Dark re-grounds on the neutral ramp and lifts the accent one step to
  // accent-500, which holds contrast on ink where the light accent does not.
  static const Color darkBg = Color(0xFF1A1918);
  static const Color darkSurface = Color(0xFF262424);
  static const Color darkSurfaceAlt = Color(0xFF302E2E);
  static const Color darkText = Color(0xFFF3F2F2);
  static const Color darkAccent = accent500;
  static const Color darkOnAccent = Color(0xFF1A1918);
  static const Color darkSkeleton = Color(0xFF3A3737);
  static const Color darkSkeletonAlt = Color(0xFF2E2C2C);

  /// Alpha the dark theme applies to [darkText] to draw a divider.
  static const double darkDividerAlpha = 0.30;

  // ── Status palette ──────────────────────────────────────────────────────
  // A documented extension to the mono system: the API carries five request
  // states that must be told apart at a glance. Two hues are added at the
  // accent ramp's perceptual lightness; rejection reuses the accent, so red
  // never reads as "fine".
  static const Color lightPendingBg = Color(0xFFFFF3DC);
  static const Color lightPendingFg = Color(0xFFA66300);
  static const Color lightApprovedBg = Color(0xFFE1F3E7);
  static const Color lightApprovedFg = Color(0xFF1E7A46);
  static const Color lightRejectedBg = accent200;
  static const Color lightRejectedFg = accent700;
  static const Color lightPaidBg = neutral900;
  static const Color lightPaidFg = Color(0xFFF3F2F2);
  static const Color lightMutedBg = neutral200;
  static const Color lightMutedFg = neutral800;

  static const Color darkPendingBg = Color(0xFF3A2C12);
  static const Color darkPendingFg = Color(0xFFF5B94D);
  static const Color darkApprovedBg = Color(0xFF14301F);
  static const Color darkApprovedFg = Color(0xFF5FD08D);
  static const Color darkRejectedBg = Color(0xFF3A1A14);
  static const Color darkRejectedFg = Color(0xFFFF7A62);
  static const Color darkPaidBg = neutral300;
  static const Color darkPaidFg = Color(0xFF1A1918);
  static const Color darkMutedBg = Color(0xFF302E2E);
  static const Color darkMutedFg = neutral400;

  // ── Elevation ───────────────────────────────────────────────────────────
  /// Ink the three shadow levels are tinted with.
  static const Color shadow = neutral900;
}
