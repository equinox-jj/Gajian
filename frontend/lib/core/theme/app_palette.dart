import 'package:flutter/material.dart';

import 'app_colors.dart';

/// The theme-dependent half of the design system: semantic roles whose value
/// changes between light and dark.
///
/// Widgets read roles from here — `context.palette.surface`, never
/// `AppColors.lightSurface` — so a screen renders correctly in both themes
/// without branching on brightness.
@immutable
class AppPalette extends ThemeExtension<AppPalette> {
  const AppPalette({
    required this.bg,
    required this.surface,
    required this.surfaceAlt,
    required this.text,
    required this.textLabel,
    required this.textMuted,
    required this.accent,
    required this.accentSecondary,
    required this.onAccent,
    required this.divider,
    required this.skeleton,
    required this.skeletonAlt,
    required this.pendingBg,
    required this.pendingFg,
    required this.approvedBg,
    required this.approvedFg,
    required this.rejectedBg,
    required this.rejectedFg,
    required this.paidBg,
    required this.paidFg,
    required this.mutedBg,
    required this.mutedFg,
  });

  /// Page ground.
  final Color bg;

  /// Raised block on the ground — cards, list containers, fields.
  final Color surface;

  /// One step above [surface], for a block nested inside one.
  final Color surfaceAlt;

  /// Primary ink.
  final Color text;

  /// Ink at 70% — field labels.
  final Color textLabel;

  /// Ink at 55% — secondary copy, captions, `.text-muted`.
  final Color textMuted;

  /// The single accent voice.
  final Color accent;

  /// Secondary accent, used sparingly.
  final Color accentSecondary;

  /// Ink that sits on top of [accent].
  final Color onAccent;

  /// Ink at 40% (light) / 30% (dark) — rules and field borders.
  final Color divider;

  /// Skeleton block while a list loads.
  final Color skeleton;

  /// The lighter of the two skeleton fills, for the secondary line of a row.
  final Color skeletonAlt;

  final Color pendingBg;
  final Color pendingFg;
  final Color approvedBg;
  final Color approvedFg;
  final Color rejectedBg;
  final Color rejectedFg;
  final Color paidBg;
  final Color paidFg;

  /// Cancelled, and the attendance states that carry no judgement (cuti/izin).
  final Color mutedBg;
  final Color mutedFg;

  static const AppPalette light = AppPalette(
    bg: AppColors.lightBg,
    surface: AppColors.lightSurface,
    surfaceAlt: AppColors.lightSurfaceAlt,
    text: AppColors.lightText,
    textLabel: Color.fromRGBO(0x20, 0x1E, 0x1D, 0.70),
    textMuted: Color.fromRGBO(0x20, 0x1E, 0x1D, 0.55),
    accent: AppColors.lightAccent,
    accentSecondary: AppColors.accentSecondary,
    onAccent: AppColors.lightOnAccent,
    divider: Color.fromRGBO(0x20, 0x1E, 0x1D, AppColors.lightDividerAlpha),
    skeleton: AppColors.lightSkeleton,
    skeletonAlt: AppColors.lightSkeletonAlt,
    pendingBg: AppColors.lightPendingBg,
    pendingFg: AppColors.lightPendingFg,
    approvedBg: AppColors.lightApprovedBg,
    approvedFg: AppColors.lightApprovedFg,
    rejectedBg: AppColors.lightRejectedBg,
    rejectedFg: AppColors.lightRejectedFg,
    paidBg: AppColors.lightPaidBg,
    paidFg: AppColors.lightPaidFg,
    mutedBg: AppColors.lightMutedBg,
    mutedFg: AppColors.lightMutedFg,
  );

  static const AppPalette dark = AppPalette(
    bg: AppColors.darkBg,
    surface: AppColors.darkSurface,
    surfaceAlt: AppColors.darkSurfaceAlt,
    text: AppColors.darkText,
    textLabel: Color.fromRGBO(0xF3, 0xF2, 0xF2, 0.70),
    textMuted: Color.fromRGBO(0xF3, 0xF2, 0xF2, 0.55),
    accent: AppColors.darkAccent,
    accentSecondary: AppColors.accentSecondary500,
    onAccent: AppColors.darkOnAccent,
    divider: Color.fromRGBO(0xF3, 0xF2, 0xF2, AppColors.darkDividerAlpha),
    skeleton: AppColors.darkSkeleton,
    skeletonAlt: AppColors.darkSkeletonAlt,
    pendingBg: AppColors.darkPendingBg,
    pendingFg: AppColors.darkPendingFg,
    approvedBg: AppColors.darkApprovedBg,
    approvedFg: AppColors.darkApprovedFg,
    rejectedBg: AppColors.darkRejectedBg,
    rejectedFg: AppColors.darkRejectedFg,
    paidBg: AppColors.darkPaidBg,
    paidFg: AppColors.darkPaidFg,
    mutedBg: AppColors.darkMutedBg,
    mutedFg: AppColors.darkMutedFg,
  );

  @override
  AppPalette copyWith({
    Color? bg,
    Color? surface,
    Color? surfaceAlt,
    Color? text,
    Color? textLabel,
    Color? textMuted,
    Color? accent,
    Color? accentSecondary,
    Color? onAccent,
    Color? divider,
    Color? skeleton,
    Color? skeletonAlt,
    Color? pendingBg,
    Color? pendingFg,
    Color? approvedBg,
    Color? approvedFg,
    Color? rejectedBg,
    Color? rejectedFg,
    Color? paidBg,
    Color? paidFg,
    Color? mutedBg,
    Color? mutedFg,
  }) => AppPalette(
    bg: bg ?? this.bg,
    surface: surface ?? this.surface,
    surfaceAlt: surfaceAlt ?? this.surfaceAlt,
    text: text ?? this.text,
    textLabel: textLabel ?? this.textLabel,
    textMuted: textMuted ?? this.textMuted,
    accent: accent ?? this.accent,
    accentSecondary: accentSecondary ?? this.accentSecondary,
    onAccent: onAccent ?? this.onAccent,
    divider: divider ?? this.divider,
    skeleton: skeleton ?? this.skeleton,
    skeletonAlt: skeletonAlt ?? this.skeletonAlt,
    pendingBg: pendingBg ?? this.pendingBg,
    pendingFg: pendingFg ?? this.pendingFg,
    approvedBg: approvedBg ?? this.approvedBg,
    approvedFg: approvedFg ?? this.approvedFg,
    rejectedBg: rejectedBg ?? this.rejectedBg,
    rejectedFg: rejectedFg ?? this.rejectedFg,
    paidBg: paidBg ?? this.paidBg,
    paidFg: paidFg ?? this.paidFg,
    mutedBg: mutedBg ?? this.mutedBg,
    mutedFg: mutedFg ?? this.mutedFg,
  );

  @override
  AppPalette lerp(covariant ThemeExtension<AppPalette>? other, double t) {
    if (other is! AppPalette) return this;
    return AppPalette(
      bg: Color.lerp(bg, other.bg, t)!,
      surface: Color.lerp(surface, other.surface, t)!,
      surfaceAlt: Color.lerp(surfaceAlt, other.surfaceAlt, t)!,
      text: Color.lerp(text, other.text, t)!,
      textLabel: Color.lerp(textLabel, other.textLabel, t)!,
      textMuted: Color.lerp(textMuted, other.textMuted, t)!,
      accent: Color.lerp(accent, other.accent, t)!,
      accentSecondary: Color.lerp(accentSecondary, other.accentSecondary, t)!,
      onAccent: Color.lerp(onAccent, other.onAccent, t)!,
      divider: Color.lerp(divider, other.divider, t)!,
      skeleton: Color.lerp(skeleton, other.skeleton, t)!,
      skeletonAlt: Color.lerp(skeletonAlt, other.skeletonAlt, t)!,
      pendingBg: Color.lerp(pendingBg, other.pendingBg, t)!,
      pendingFg: Color.lerp(pendingFg, other.pendingFg, t)!,
      approvedBg: Color.lerp(approvedBg, other.approvedBg, t)!,
      approvedFg: Color.lerp(approvedFg, other.approvedFg, t)!,
      rejectedBg: Color.lerp(rejectedBg, other.rejectedBg, t)!,
      rejectedFg: Color.lerp(rejectedFg, other.rejectedFg, t)!,
      paidBg: Color.lerp(paidBg, other.paidBg, t)!,
      paidFg: Color.lerp(paidFg, other.paidFg, t)!,
      mutedBg: Color.lerp(mutedBg, other.mutedBg, t)!,
      mutedFg: Color.lerp(mutedFg, other.mutedFg, t)!,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AppPalette &&
          other.bg == bg &&
          other.surface == surface &&
          other.surfaceAlt == surfaceAlt &&
          other.text == text &&
          other.textLabel == textLabel &&
          other.textMuted == textMuted &&
          other.accent == accent &&
          other.accentSecondary == accentSecondary &&
          other.onAccent == onAccent &&
          other.divider == divider &&
          other.skeleton == skeleton &&
          other.skeletonAlt == skeletonAlt &&
          other.pendingBg == pendingBg &&
          other.pendingFg == pendingFg &&
          other.approvedBg == approvedBg &&
          other.approvedFg == approvedFg &&
          other.rejectedBg == rejectedBg &&
          other.rejectedFg == rejectedFg &&
          other.paidBg == paidBg &&
          other.paidFg == paidFg &&
          other.mutedBg == mutedBg &&
          other.mutedFg == mutedFg;

  @override
  int get hashCode => Object.hashAll(<Object>[
    bg,
    surface,
    surfaceAlt,
    text,
    textLabel,
    textMuted,
    accent,
    accentSecondary,
    onAccent,
    divider,
    skeleton,
    skeletonAlt,
    pendingBg,
    pendingFg,
    approvedBg,
    approvedFg,
    rejectedBg,
    rejectedFg,
    paidBg,
    paidFg,
    mutedBg,
    mutedFg,
  ]);
}

extension AppPaletteX on BuildContext {
  /// The active [AppPalette].
  ///
  /// Throws if no Gajian theme is in scope, which is a wiring bug rather than
  /// a runtime condition worth a silent fallback.
  AppPalette get palette => Theme.of(this).extension<AppPalette>()!;
}
