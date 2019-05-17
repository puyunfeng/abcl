package qsos.lib.base.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * @author : 华清松
 * @description : ViewModel 工具类
 */
object ViewModelUtils {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified E : ViewModel> getModel(fragment: Fragment, c: E): E {
        return ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
            override fun <E : ViewModel> create(modelClass: Class<E>): E {
                return c as E
            }
        })[E::class.java]
    }
}