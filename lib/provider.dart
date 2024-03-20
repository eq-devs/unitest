import 'package:unitest/uni.dart';
import 'package:flutter/material.dart';

class UniProvider extends ChangeNotifier {
  UniProvider({required this.uniAppService});

  final UniAppService uniAppService;

  Future<void> init() async {
    try {
      await uniAppService.init();
    } catch (e) {
      print(e);
    }
  }
}
