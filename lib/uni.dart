import 'dart:async';
import 'package:flutter/services.dart';

class UniAppService {
  final MethodChannel _channel = const MethodChannel('flutter_uni');
  final EventChannel _stream = const EventChannel('flutter_uni_stream');

  StreamController? _controller;
  Stream<dynamic>? _streamInstance;

  Stream<dynamic>? get uniStream {
    if (_streamInstance == null) {
      _controller = StreamController.broadcast();
      _streamInstance = _controller!.stream;
      _stream.receiveBroadcastStream().listen((event) {
        _controller!.add(event);
      }, onError: (err) {
        _controller!.addError(err);
      });
    }
    return _streamInstance;
  }

  /// 初始化uniMPSDK
  Future<dynamic> init({void Function(dynamic)? receive}) async {
    uniStream?.listen((event) async {
      if (receive != null) {
        receive(event);
      }
    }, onError: (err) {
      print('Error occurred: $err');
    });
    final result = await _channel.invokeMethod('initMP');
    return result;
  }

  /// 检查指定的 UniMP 小程序
  Future<dynamic> checkMP({required String appid}) async {
    final result = await _channel.invokeMethod('checkMP', {'appid': appid});
    return result;
  }

  /// 获取指定的 UniMP 小程序版本
  Future<dynamic> versionMP({required String appid}) async {
    final result = await _channel.invokeMethod('versionMP', {'appid': appid});
    return result;
  }

  /// 安装 UniMP 小程序
  Future<dynamic> installMP(
      {required String appid, required String wgtPath}) async {
    final result = await _channel
        .invokeMethod('installMP', {'appid': appid, "wgtPath": wgtPath});
    return result;
  }

  /// 打开指定的 UniMP 小程序
  Future<dynamic> openMP({
    required String appid,
    Map<String, dynamic>? config,
  }) async {
    final result = await _channel
        .invokeMethod('openMP', {'appid': appid, 'config': config});
    return result;
  }

  /// 关闭指定的 UniMP 小程序
  Future<void> closeMP({required String appid}) async {
    final result = await _channel.invokeMethod('closeMP', {'appid': appid});
    return result;
  }

  /// 隐藏指定的 UniMP 小程序
  Future<void> hideMP({required String appid}) async {
    final result = await _channel.invokeMethod('hideMP', {'appid': appid});
    return result;
  }

  /// 发送数据到指定的UniMP小程序
  Future<void> sendMP({
    required String appid,
    required String event,
    Map<String, dynamic>? data,
  }) async {
    final result = await _channel.invokeMethod('sendMP', {
      'appid': appid,
      'event': event,
      'data': data ?? {},
    });
    return result;
  }

  /// 回调数据到到指定的UniMP小程序
  Future<void> callbackMP({
    required String appid,
    required String event,
    Map<String, dynamic>? data,
  }) async {
    final result = await _channel.invokeMethod('callbackMP', {
      'appid': appid,
      'event': event,
      'data': data ?? {},
    });
    return result;
  }
}
