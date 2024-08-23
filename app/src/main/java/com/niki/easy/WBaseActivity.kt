package com.niki.easy

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * free you from setting databinding!
 */
abstract class WBaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    private val TAG: String = this::class.java.name

    /**
     * in your implement, you can directly call members by their ids in your layout
     * - " myTextView.text = myString "
     * - " binding = this@initBinding "
     */
   abstract fun VB.initBinding()

    private fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return inflate.invoke(null, inflater) as VB
    }

    private val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }

    /**
     * initBinding() is in child's super.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.initBinding()
    }
}