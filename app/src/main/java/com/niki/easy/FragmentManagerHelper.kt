package com.niki.easy

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * a simple implement for FragmentManagerHelper
 */
class FragmentManagerHelper(
    private val fragmentManager: FragmentManager,
    private val containerViewId: Int,
    private val fragments: List<Fragment>
) {
    private val TAG = this::class.java.name
    private var currentIndex = 0

    init {
        initializeFragments()
    }

    /**
     * add all fragments,
     * then left over the index 0
     */
    private fun initializeFragments() {
        // made by ai which is crazy
        fragmentManager.beginTransaction().apply {
            fragments.forEachIndexed { index, fragment ->
                add(containerViewId, fragment, fragment.javaClass.name)
                if (index != 0) hide(fragment)
            }
        }.commit()
    }

    fun switchToFragment(index: Int) {
        if (index < 0 || index >= fragments.size) {
            Log.e(TAG, "Invalid index: $index")
            return
        }

        if (index == currentIndex) return

        fragmentManager.beginTransaction().apply {
            hide(fragments[currentIndex])
            show(fragments[index])
        }.commit()

        currentIndex = index
    }

}
