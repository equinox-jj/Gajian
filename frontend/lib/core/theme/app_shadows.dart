import 'package:flutter/painting.dart';

/// The three elevation levels (`--shadow-sm|md|lg`).
///
/// Elevation appears only where something floats over content: bottom sheets
/// and dialogs take [lg], toasts take [md]. Cards and rows sit flat on the
/// ground and take none.
abstract final class AppShadows {
  const AppShadows._();

  static const Color _ink = Color(0xFF2D2B2B);

  static const List<BoxShadow> none = <BoxShadow>[];

  static const List<BoxShadow> sm = <BoxShadow>[
    BoxShadow(
      color: Color.fromRGBO(0x2D, 0x2B, 0x2B, 0.14),
      offset: Offset(0, 1),
      blurRadius: 2,
    ),
  ];

  static const List<BoxShadow> md = <BoxShadow>[
    BoxShadow(
      color: Color.fromRGBO(0x2D, 0x2B, 0x2B, 0.16),
      offset: Offset(0, 3),
      blurRadius: 10,
    ),
  ];

  static const List<BoxShadow> lg = <BoxShadow>[
    BoxShadow(
      color: Color.fromRGBO(0x2D, 0x2B, 0x2B, 0.22),
      offset: Offset(0, 12),
      blurRadius: 32,
    ),
  ];

  /// Ink the levels above are tinted with, for the Material `shadowColor`
  /// slots that take a bare colour rather than a [BoxShadow].
  static const Color ink = _ink;
}
