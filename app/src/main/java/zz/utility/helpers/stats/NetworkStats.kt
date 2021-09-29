package zz.utility.helpers.stats

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Process
import android.telephony.*

class NetworkStats(
    var mobileRx: Long,
    var mobileTx: Long,
    var totalRx: Long,
    var totalTx: Long,
    var appRx: Long,
    var appTx: Long,
    var serviceStateDescription: String,
    var operatorName: String,
    var cellType: String,
    var isEmergencyOnly: Boolean,
    var isInService: Boolean,
    var isOutOfService: Boolean,
    var isPowerOff: Boolean,
    var isWifiConnected: Boolean,
    var isMobileConnected: Boolean,
    var signalStrength: Int
)

@SuppressLint("MissingPermission")
fun Activity.getNetworkStats(): NetworkStats {
    val ss = ServiceState()

    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

    var cellType = ""
    var signalStrength = 0
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    for (c in telephonyManager.allCellInfo) {
        when (c) {
            is CellInfoGsm -> {
                cellType = "GSM"
                signalStrength = c.cellSignalStrength.dbm
            }
            is CellInfoLte -> {
                cellType = "LTE"
                signalStrength = c.cellSignalStrength.dbm
            }
            is CellInfoCdma -> {
                cellType = "CDMA"
                signalStrength = c.cellSignalStrength.dbm
            }
            is CellInfoWcdma -> {
                cellType = "WCDMA"
                signalStrength = c.cellSignalStrength.dbm
            }
        }
    }

    return NetworkStats(
        TrafficStats.getMobileRxBytes(),
        TrafficStats.getMobileTxBytes(),
        TrafficStats.getTotalRxBytes(),
        TrafficStats.getTotalTxBytes(),
        TrafficStats.getUidRxBytes(Process.myUid()),
        TrafficStats.getUidTxBytes(Process.myUid()),
        when (ss.state) {
            ServiceState.STATE_EMERGENCY_ONLY -> "Emergency Only"
            ServiceState.STATE_IN_SERVICE -> "In Service"
            ServiceState.STATE_OUT_OF_SERVICE -> "Out of Service"
            ServiceState.STATE_POWER_OFF -> "Cell Powered Off"
            else -> {
                "Unknown"
            }
        },
        ss.operatorAlphaLong ?: "",
        cellType,
        ss.state == ServiceState.STATE_EMERGENCY_ONLY,
        ss.state == ServiceState.STATE_IN_SERVICE,
        ss.state == ServiceState.STATE_OUT_OF_SERVICE,
        ss.state == ServiceState.STATE_POWER_OFF,
        wifi != null && wifi.isAvailable && wifi.isConnected,
        mobile != null && mobile.isAvailable && mobile.isConnectedOrConnecting,
        signalStrength
    )
}