package com.example.unitest

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class Service {

    private fun handleMethod(call: MethodCall, result: MethodChannel.Result) {
        val methodName = call.argument<String>("methodName")
        when (methodName) {
            "initMP" -> {
                initMP(result)
            }
            "checkMP" -> {
                checkMP(result)
            }
            "versionMP" -> {
                versionMP(result)
            }
            "installMP" -> {
                installMP(call.argument<String>("appid"), call.argument<String>("wgtPath"), result)
            }
            "openMP" -> {
                openMP(call.argument<String>("appid"), call.argument<Map<String, Any>>("config"), result)
            }
            "hideMP" -> {
                hideMP(call.argument<String>("appid"), result)
            }
            "closeMP" -> {
                closeMP(call.argument<String>("appid"), result)
            }
            "sendMP" -> {
                sendMP(call.argument<String>("appid"), call.argument<String>("event"), call.argument<Any>("data"), result)
            }
            "callbackMP" -> {
                callbackMP(call.argument<String>("appid"), call.argument<String>("event"), call.argument<Any>("data"), result)
            }
            else -> {
                result.error("UNAVAILABLE", "Method not implemented", null)
            }
        }
    }

    private fun initMP(result: MethodChannel.Result) {
        // Implement initMP function
        // You can access context variable as this.context
        // You can use result.success(data), result.error(errorCode, errorMessage, data), result.notImplemented()
    }

    private fun checkMP(result: MethodChannel.Result) {
        // Implement checkMP function
    }

    private fun versionMP(result: MethodChannel.Result) {
        // Implement versionMP function
    }

    private fun installMP(appid: String?, wgtPath: String?, result: MethodChannel.Result) {
        // Implement installMP function
    }

    private fun openMP(appid: String?, config: Map<String, Any>?, result: MethodChannel.Result) {
        // Implement openMP function
    }

    private fun hideMP(appid: String?, result: MethodChannel.Result) {
        // Implement hideMP function
    }

    private fun closeMP(appid: String?, result: MethodChannel.Result) {
        // Implement closeMP function
    }

    private fun sendMP(appid: String?, event: String?, data: Any?, result: MethodChannel.Result) {
        // Implement sendMP function
    }

    private fun callbackMP(appid: String?, event: String?, data: Any?, result: MethodChannel.Result) {
        // Implement callbackMP function
    }
}