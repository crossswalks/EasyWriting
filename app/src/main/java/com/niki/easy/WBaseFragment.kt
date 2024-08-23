package com.niki.easy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class WBaseFragment<VB : ViewDataBinding> : Fragment() {
    protected val TAG: String = this::class.java.name

    private lateinit var mBinding: VB

    /**
     * in your implement, you can directly call members by their ids in your layout
     * - " myTextView.text = myString "
     * - " binding = this@initBinding "
     */
    abstract fun VB.initBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater, container)
        return mBinding.root
    }

    /**
     * initBinding() is in child's super.onViewCreated
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.initBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
        // to prevent leak
        if (::mBinding.isInitialized) {
            mBinding.unbind()
        }
    }

    private fun <VB : ViewBinding> Any.getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return inflate.invoke(null, inflater, container, false) as VB
    }
}