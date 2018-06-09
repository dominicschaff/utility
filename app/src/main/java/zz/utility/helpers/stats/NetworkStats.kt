package zz.utility.helpers.stats

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Process
import android.telephony.*

class NetworkStats {
    var mobileRx: Long = 0
    var mobileTx: Long = 0
    var totalRx: Long = 0
    var totalTx: Long = 0
    var appRx: Long = 0
    var appTx: Long = 0
    var serviceStateDescription: String = ""
    var operatorName: String = ""
    var cellType: String = ""
    var isEmergencyOnly: Boolean = false
    var isInService: Boolean = false
    var isOutOfService: Boolean = false
    var isPowerOff: Boolean = false
    var isWifiConnected: Boolean = false
    var isMobileConnected: Boolean = false
    var signalStrength: Int = 0

    companion object {

        @SuppressLint("MissingPermission")
        operator fun get(activity: Activity): NetworkStats {

            val ns = NetworkStats()
            ns.mobileRx = TrafficStats.getMobileRxBytes()
            ns.mobileTx = TrafficStats.getMobileTxBytes()
            ns.totalRx = TrafficStats.getTotalRxBytes()
            ns.totalTx = TrafficStats.getTotalTxBytes()
            ns.appRx = TrafficStats.getUidRxBytes(Process.myUid())
            ns.appTx = TrafficStats.getUidTxBytes(Process.myUid())

            val ss = ServiceState()

            ns.operatorName = ss.operatorAlphaLong ?: ""

            ns.isEmergencyOnly = ss.state == ServiceState.STATE_EMERGENCY_ONLY
            ns.isInService = ss.state == ServiceState.STATE_IN_SERVICE
            ns.isOutOfService = ss.state == ServiceState.STATE_OUT_OF_SERVICE
            ns.isPowerOff = ss.state == ServiceState.STATE_POWER_OFF

            ns.serviceStateDescription = "Unknown"
            when (ss.state) {
                ServiceState.STATE_EMERGENCY_ONLY -> ns.serviceStateDescription = "Emergency Only"
                ServiceState.STATE_IN_SERVICE -> ns.serviceStateDescription = "In Service"
                ServiceState.STATE_OUT_OF_SERVICE -> ns.serviceStateDescription = "Out of Service"
                ServiceState.STATE_POWER_OFF -> ns.serviceStateDescription = "Cell Powered Off"
                else -> {
                }
            }


            val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            ns.isWifiConnected = wifi != null && wifi.isAvailable && wifi.isConnected
            ns.isMobileConnected = mobile != null && mobile.isAvailable && mobile.isConnectedOrConnecting

            val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            for (c in telephonyManager.allCellInfo) {
                when (c) {
                    is CellInfoGsm -> {
                        ns.cellType = "GSM"
                        ns.signalStrength = c.cellSignalStrength.dbm
                    }
                    is CellInfoLte -> {
                        ns.cellType = "LTE"
                        ns.signalStrength = c.cellSignalStrength.dbm
                    }
                    is CellInfoCdma -> {
                        ns.cellType = "CDMA"
                        ns.signalStrength = c.cellSignalStrength.dbm
                    }
                    is CellInfoWcdma -> {
                        ns.cellType = "WCDMA"
                        ns.signalStrength = c.cellSignalStrength.dbm
                    }
                    else -> ns.signalStrength = 0
                }
            }

            return ns
        }
    }
}
