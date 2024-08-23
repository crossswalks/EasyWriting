package com.niki.easy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.ParameterizedType

abstract class WContentCollector<KEY, VALUE>(val max: Int = 9) {
    /**
     * cache size = $max,
     * and will delete the eldest when stepped onto $max
     */
    private val cache = object : LinkedHashMap<KEY, VALUE>(max, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<KEY, VALUE>): Boolean {
            return size > max
        }
    }

    fun get(key: KEY): VALUE? {
        return cache[key]
    }

    fun put(key: KEY, value: VALUE) {
        cache[key] = value
    }
}

/**
 * base class for adapters which is real comfortable
 * @param VB your databinding
 * @param D your dataclass
 * @param C your content for child page
 * @param diffCallback implement the class YourCallback : DiffUtil.ItemCallback<YourBean>()
 */
abstract class WBaseAdapter<VB : ViewDataBinding, D, C>(
    diffCallback: DiffUtil.ItemCallback<D>
) : ListAdapter<D, WBaseAdapter<VB, D, C>.ViewHolder>(diffCallback) {
    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    protected val collector = object : WContentCollector<D, C?>(3) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return with(getViewBinding(LayoutInflater.from(parent.context), parent, 1)) {
            ViewHolder(this)
        }
    }

    /**
     * takeover onBindViewHolder, easy to use
     */
    abstract fun VB.onBindViewHolder(bean: D, position: Int)

    /**
     * being taken over
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            onBindViewHolder(getItem(position), position)
            executePendingBindings()
        }
    }

    private fun Any.getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        position: Int = 0
    ): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[position].getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return inflate.invoke(null, inflater, container, false) as VB
    }

}