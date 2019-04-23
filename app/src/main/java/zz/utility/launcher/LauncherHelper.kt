package zz.utility.launcher

data class AppInfo(
        val label: String,
        val packageName: String
)

fun CharSequence.firstLetters(): String {
    val a = this.split(" ")
    return when (a.size) {
        0 -> "-"
        1 -> "${a[0].first()}"
        else -> "${a[0].first()}${a[1].first()}"
    }
}
