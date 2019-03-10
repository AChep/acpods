package com.artemchep.acpods.ui.dialogs

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.afollestad.materialdialogs.MaterialDialog
import com.artemchep.acpods.R
import com.artemchep.acpods.URL_REPOSITORY

fun Context.showAboutDialog() = MaterialDialog(this).show {
    val packageInfo: PackageInfo
    try {
        packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        throw RuntimeException(e)
    }

    title(text = getString(R.string.about_title, packageInfo.versionName))
    message(R.string.about_summary)
    positiveButton(R.string.close)
    negativeButton(R.string.repository) {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this@showAboutDialog, Uri.parse(URL_REPOSITORY))
    }
}
