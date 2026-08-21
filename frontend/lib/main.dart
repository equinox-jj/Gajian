import 'package:flutter/material.dart';

import 'core/theme/theme.dart';

void main() {
  runApp(const GajianApp());
}

class GajianApp extends StatelessWidget {
  const GajianApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Gajian',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      // Follows the device until the theme preference lands in secure storage.
      themeMode: ThemeMode.system,
      home: const _ThemePreview(),
    );
  }
}

/// Placeholder home, standing in until routing and the first feature land. It
/// renders one instance of each themed part so the theme can be eyeballed on a
/// device in both light and dark.
class _ThemePreview extends StatelessWidget {
  const _ThemePreview();

  @override
  Widget build(BuildContext context) {
    final palette = context.palette;
    final text = Theme.of(context).textTheme;

    return Scaffold(
      appBar: AppBar(title: const Text('Gajian')),
      body: SingleChildScrollView(
        padding: AppInsets.screen + AppInsets.section,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          spacing: AppSpacing.x4,
          children: <Widget>[
            Text('TAKE-HOME PAY', style: text.labelSmall),
            Text('Rp 8.420.000', style: text.displaySmall),
            Text('Wednesday, 12 August 2026', style: text.bodyMedium),
            const _StatusTagRow(),
            FilledButton(onPressed: () {}, child: const Text('Check in now')),
            OutlinedButton(onPressed: () {}, child: const Text('Check out')),
            TextButton(onPressed: () {}, child: const Text('Cancel request')),
            const TextField(
              decoration: InputDecoration(
                labelText: 'Reason',
                hintText: 'Family matter',
              ),
            ),
            Container(
              width: double.infinity,
              color: palette.surface,
              padding: AppInsets.card,
              child: Text('Annual Leave · 9 / 12 left', style: text.titleSmall),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatusTagRow extends StatelessWidget {
  const _StatusTagRow();

  @override
  Widget build(BuildContext context) {
    final palette = context.palette;

    return Wrap(
      spacing: AppSpacing.x2,
      runSpacing: AppSpacing.x2,
      children: <Widget>[
        _StatusTag('PENDING', bg: palette.pendingBg, fg: palette.pendingFg),
        _StatusTag('APPROVED', bg: palette.approvedBg, fg: palette.approvedFg),
        _StatusTag('REJECTED', bg: palette.rejectedBg, fg: palette.rejectedFg),
        _StatusTag('PAID', bg: palette.paidBg, fg: palette.paidFg),
        _StatusTag('CANCELLED', bg: palette.mutedBg, fg: palette.mutedFg),
      ],
    );
  }
}

class _StatusTag extends StatelessWidget {
  const _StatusTag(this.label, {required this.bg, required this.fg});

  final String label;
  final Color bg;
  final Color fg;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: bg,
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.x2,
        vertical: AppSpacing.x1,
      ),
      child: Text(
        label,
        style: Theme.of(context).textTheme.bodySmall
            ?.copyWith(color: fg, fontWeight: AppFontWeight.semiBold),
      ),
    );
  }
}
