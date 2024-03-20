package com.example.unitest

import android.util.Log
import com.alibaba.fastjson.JSONObject
import io.dcloud.feature.sdk.DCSDKInitConfig
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.IUniMP
import io.dcloud.feature.sdk.MenuActionSheetItem
import io.dcloud.feature.unimp.DCUniMPJSCallback
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterFragmentActivity() {


    private val unimpMap = mutableMapOf<String?, IUniMP?>();
    private var uniMpcallback: DCUniMPJSCallback? = null
    var eventSink: EventChannel.EventSink? = null;

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        val messenger = flutterEngine.dartExecutor.binaryMessenger

        val event = EventChannel(messenger, "flutter_uni_stream")
        val channel = MethodChannel(messenger, "flutter_uni")

        setupEvenChannel(event)
        setupMethodChannel(channel)


    }


    private fun setupEvenChannel(eventChannel: EventChannel) {
        eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                eventSink = events;
                Log.w("Android", "EventChannel onCancel called")
            }

            override fun onCancel(arguments: Any?) {
                eventSink?.endOfStream();
                Log.d("Android", "EventChannel onListen called")
            }
        });
    }

    private fun setupMethodChannel(channel: MethodChannel) {
        channel.setMethodCallHandler { call, res ->
            // 根据方法名，分发不同的处理
            when (call.method) {
                "initMP" -> {
                    try {
                        if (DCUniMPSDK.getInstance().isInitialize) {
                            res.success(2)
                        } else {
                            // 初始化uniMPSDK
                            val item = MenuActionSheetItem("关于", "about")
                            val sheetItems: MutableList<MenuActionSheetItem> = ArrayList()
                            sheetItems.add(item)
                            // 初始化uniMPSDK
                            val config = DCSDKInitConfig.Builder()
                                .setCapsule(true)
                                .setMenuDefFontSize("16px")
                                .setMenuDefFontColor("#2D2D2D")
                                .setMenuDefFontWeight("normal")
                                .setMenuActionSheetItems(sheetItems)
                                .build()

                            DCUniMPSDK.getInstance().initialize(this, config)

                            //监听胶囊点击事件
                            DCUniMPSDK.getInstance()
                                .setCapsuleMenuButtonClickCallBack { argumentAppID ->
                                    val backdata = JSONObject().apply {
                                        set("appid", argumentAppID)
                                        set("event", "capsuleaction")
                                    }
                                    eventSink?.success(backdata)
                                }

                            // 监听小程序关闭
                            DCUniMPSDK.getInstance().setUniMPOnCloseCallBack { argumentAppID ->
                                if (unimpMap.containsKey(argumentAppID)) {
                                    unimpMap.remove(argumentAppID)
                                    unimpMap[argumentAppID]?.closeUniMP();
                                }
                                val backdata = JSONObject().apply {
                                    set("appid", argumentAppID)
                                    set("event", "close")
                                }
                                eventSink?.success(backdata)
                            }

                            //监听小程序向原生发送事件回调方法
                            DCUniMPSDK.getInstance()
                                .setOnUniMPEventCallBack { argumentAppID, event, data, callback ->
                                    val backdata = JSONObject().apply {
                                        set("appid", argumentAppID)
                                        set("event", event)
                                        set("data", data)
                                    }
                                    eventSink?.success(backdata)
                                    uniMpcallback = callback
                                }

                            res.success(1)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 检查指定的 UniMP 小程序
                 * {
                 *      "appid": ""
                 * }
                 */
                "checkMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        if (DCUniMPSDK.getInstance().isExistsApp(argumentAppID)) {
                            res.success(true)
                        } else {
                            res.success(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 获取指定的 UniMP 小程序版本
                 * {
                 *      "appid": ""
                 * }
                 */
                "versionMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        val result = DCUniMPSDK.getInstance().getAppVersionInfo(argumentAppID)
                        var versionStr: String? = null;
                        if (result != null) {
                            versionStr = result.toString()
                        }
                        res.success(versionStr)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 安装 UniMP 小程序
                 * {
                 *      "appid": ""，
                 *      "wgtPath": ""
                 * }
                 */
                "installMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        val wgtPath: String? = call.argument<String>("wgtPath")
                        val releaseConfig = UniMPReleaseConfiguration()
                        releaseConfig.wgtPath = wgtPath
                        DCUniMPSDK.getInstance().releaseWgtToRunPath(
                            argumentAppID,
                            releaseConfig
                        ) { code, result ->
                            if (code == 1) {
                                res.success(true)
                            } else {
                                res.success(false)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 打开指定的 UniMP 小程序
                 * {
                 *      "appid": "",
                 *      "isreload": true //重新打开
                 *      "config": {
                 *          "extraData": {},  //其他自定义参数JSON
                 *          "path": "" //指定启动应用后直接打开的页面路径
                 *      }
                 * }
                 */
                "openMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        if (!unimpMap.containsKey(argumentAppID)) {
                            val argumentConfig: HashMap<String, Any>? =
                                call.argument<HashMap<String, Any>>("config")
                            val uniMPOpenConfiguration = UniMPOpenConfiguration()
                            if (argumentConfig != null && argumentConfig.containsKey("extraData")) {
                                val jsonObject = org.json.JSONObject()
                                val extraData =
                                    argumentConfig["extraData"] as HashMap<String, Any>
                                extraData.forEach { (s, any) -> jsonObject.put(s, any) }
                                jsonObject.put("path", argumentConfig.get("path") as String?)
                                uniMPOpenConfiguration.extraData = jsonObject
                            }
                            if (argumentConfig != null && argumentConfig.containsKey("path")) {
                                uniMPOpenConfiguration.path = argumentConfig.get("path") as String?
                            }
                            // 打开小程序
                            unimpMap[argumentAppID] = DCUniMPSDK.getInstance()
                                .openUniMP(
                                    applicationContext,
                                    argumentAppID,
                                    uniMPOpenConfiguration
                                )
                            res.success(true)
                        } else {
                            val data = call.argument<Any>("config")
                            val backdata = JSONObject().apply {
                                set("appid", argumentAppID)
                                set("data", data)
                            }
                            unimpMap[argumentAppID]?.sendUniMPEvent("open_app", backdata)
                            unimpMap[argumentAppID]?.showUniMP();
                            res.success(true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 隐藏指定的 UniMP 小程序
                 * {
                 *      "appid": "",
                 * }
                 */
                "hideMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        if (unimpMap.containsKey(argumentAppID)) {
                            unimpMap[argumentAppID]?.hideUniMP();
                        }
                        res.success(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 关闭指定的 UniMP 小程序
                 * {
                 *      "appid": "",
                 * }
                 */
                "closeMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID: String? = call.argument<String>("appid")
                        if (unimpMap.containsKey(argumentAppID)) {
                            unimpMap.remove(argumentAppID)
                            unimpMap[argumentAppID]?.closeUniMP();
                        }
                        res.success(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /**发送数据到指定的UniMP小程序
                 * {
                 *      "appid": "",
                 *      "event": "",
                 *      "data": {}
                 * }
                 */
                "sendMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID = call.argument<String>("appid")
                        val sendEvent = call.argument<String>("event")
                        val data = call.argument<Any>("data")

                        val backdata = JSONObject().apply {
                            set("appid", argumentAppID)
                            set("event", sendEvent)
                            set("data", data)
                        }
                        unimpMap[argumentAppID]?.sendUniMPEvent(sendEvent, backdata)
                        res.success(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                /** 回调数据到到指定的UniMP小程序
                 * {
                 *      "appid": "",
                 *      "event": "",
                 *      "data": {}
                 * }
                 */
                "callbackMP" -> {
                    try {
                        // 接收 Flutter 传入的参数
                        val argumentAppID = call.argument<String>("appid")
                        val sendEvent = call.argument<String>("event")
                        val data = call.argument<Any>("data")

                        val backdata = JSONObject().apply {
                            set("appid", argumentAppID)
                            set("event", sendEvent)
                            set("data", data)
                        }
                        uniMpcallback?.invoke(backdata)
                        res.success(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        res.error("error_code", e.message, e.printStackTrace().toString())
                    }
                }

                else -> {
                    // 如果有未识别的方法名，通知执行失败
                    res.error("error_code", "error_message", null)
                }
            }
        }
    }


    //        event.setStreamHandler(
//            object : EventChannel.StreamHandler {
//                override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
//                    eventSink = events
//                    Log.d("Android", "EventChannel onListen called")
//                }
//
//                override fun onCancel(arguments: Any?) {
//                    Log.w("Android", "EventChannel onCancel called")
//                }
//            })


}
