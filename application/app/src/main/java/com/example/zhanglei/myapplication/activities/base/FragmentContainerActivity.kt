package com.example.zhanglei.myapplication.activities.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.zhanglei.myapplication.R

class FragmentContainerActivity : BaseActivity() {
    protected val touchEvent by lazy {
        MutableLiveData<MotionEvent?>()
    }

    protected var onBackPressedListener: () -> Boolean = { false }

    override val layoutResId: Int?
        get() = R.layout.activity_fragment_container

    override fun onViewCreated(savedInstanceState: Bundle?) {
        val extras = intent.extras
        val fragmentClass = extras?.getSerializable(DESTINATION_SCREEN) as? Class<Fragment>
                ?: throw Exception("传入的目标fragment不可为空")
        val fragmentArgument = extras.getBundle(DESTINATION_ARGUMENTS)

        val fragment = fragmentClass.getConstructor().newInstance()
        fragment.arguments = fragmentArgument
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.container, fragment)
        beginTransaction.commit()
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        touchEvent.value = ev
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (!onBackPressedListener()) {
            super.onBackPressed()
        }
    }
}

private val DESTINATION_SCREEN = "目标画面"
private val DESTINATION_ARGUMENTS = "目标画面参数"

fun Activity.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    val intent = Intent(this, FragmentContainerActivity::class.java)
    intent.putExtra(DESTINATION_SCREEN, fragmentClass)
    if (extras != null) {
        intent.putExtra(DESTINATION_ARGUMENTS, extras)
    }
    this.startActivity(intent)
}

fun Fragment.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    requireActivity().startFragment(fragmentClass, extras)
}
