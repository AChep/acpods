package com.artemchep.acpods

import android.app.Application

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {

    val sessionManager by lazy { SessionManager(this) }

}
