package com.example.zhanglei.myapplication.fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zhanglei.myapplication.*
import com.example.zhanglei.myapplication.adapters.BaseAbstractAdapter
import com.example.zhanglei.myapplication.databinding.FragmentMainBinding
import com.example.zhanglei.myapplication.entities.MainMenu
import com.example.zhanglei.myapplication.entities.mainMenuList
import com.example.zhanglei.myapplication.fragments.base.ViewBindingBaseFragment
import com.google.android.material.button.MaterialButton
import com.hl.downloader.DownloadListener
import com.hl.downloader.DownloadManager
import com.hl.downloader.DownloadManager.cancelDownload
import com.hl.downloader.DownloadManager.startDownLoad
import com.hl.shadow.Shadow
import com.hl.utils.MyNetworkCallback
import com.hl.utils.reqPermissions
import com.tencent.shadow.dynamic.host.EnterCallback
import com.tencent.shadow.dynamic.host.PluginManager
import io.dcloud.feature.sdk.DCUniMPSDK
import org.jetbrains.anko.Android
import java.lang.Math.PI
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


/**
 * @author 张磊
 */
class MainFragment : ViewBindingBaseFragment<FragmentMainBinding>() {

    private companion object {
        private const val TAG = "MainActivity"
        private var permissionsList = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE
        )
        private const val REQUEST_CODE = 1
    }

    private var myAnimatorSet: MyAnimatorSet? = null

    private var myView: MyView? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun FragmentMainBinding.onViewCreated(savedInstanceState: Bundle?) {
        if (this@MainFragment.toolbar != null) {
            this@MainFragment.toolbar?.title = "主页"
        }

        MyNetworkCallback.addNetworkListener(object : com.hl.utils.NetworkListener() {
            override fun onWifiChanged(isConnected: Boolean) {
                println("wifi 改变， isConnected == $isConnected")
                if (isConnected) {
                    DownloadManager.resumeDownLoad()
                } else {
                    DownloadManager.pauseDownload()
                }
            }
        })

        this@MainFragment.myView = viewBinding?.myView
        this@MainFragment.myAnimatorSet = MyAnimatorSet()
        this.myView.setOnClickListener(object : ClickHelperListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onDoubleClick(v: View) {
                myView.fixedColor()
                myAnimatorSet?.pause()
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onSingleClick(v: View) {
                myView.changeColor()
                myAnimatorSet?.resume()
            }
        })
        this.menuRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        this.menuRecyclerView.adapter = object : BaseAbstractAdapter<MainMenu>(mainMenuList) {
            override val itemLayout: Int
                get() = R.layout.item_main_menu
        }.apply {

            this.onViewHolderInitListener = { viewHolder, _, data ->
                viewHolder.getView<MaterialButton>(R.id.main_menu_button)?.run {
                    setOnClickListener {
                        onClick(data)
                    }

                    if (data is MainMenu.DownLoadAction) {
                        setOnLongClickListener {
                            cancelDownload()
                            true
                        }
                    }
                }
            }
            this.onBindItemListener = { viewHolder, data ->
                val button = viewHolder.getView<MaterialButton>(R.id.main_menu_button)
                button?.icon = data?.icon?.run { ContextCompat.getDrawable(requireContext(), this) }
                button?.text = data?.title
            }
        }

    }

    private fun setScaleAnimation() {
        Log.d(TAG, "setScaleAnimation: ")
        val animatorX = ObjectAnimator()
        animatorX.setPropertyName("scaleX")
        animatorX.setFloatValues(0.1f)
        animatorX.duration = 5000
        animatorX.target = myView
        val animatorY = ObjectAnimator()
        animatorY.setPropertyName("scaleY")
        animatorY.setFloatValues(0.1f)
        animatorY.duration = 5000
        animatorY.target = myView
        Log.d(TAG, "setScaleAnimation: myView == $myView")
        myAnimatorSet?.setPlayWithAnimator(animatorX, animatorY)
        myAnimatorSet?.start()
    }

    private fun setRotateAnimation() {
        Log.d(TAG, "setRotateAnimation: ")
        val animator = ObjectAnimator()
        animator.setPropertyName("rotation")
        animator.setFloatValues(360f)
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.duration = 2000
        animator.target = myView
        myAnimatorSet?.setAfterAnimator(animator)
        val animator2 = ObjectAnimator()
        animator2.setPropertyName("translationX")
        animator2.interpolator = LinearInterpolator()
        animator2.duration = 500
        animator2.target = myView
        val startPoint = Point(0f, 0f)
        val endPoint = Point(200f, 200f)
        val animator1 = ValueAnimator.ofObject(PointEvaluator(), startPoint, endPoint)
        animator1.duration = 5000
        animator1.addUpdateListener { animation: ValueAnimator ->
            val point = animation.animatedValue as Point
            Log.d(TAG, "onAnimationUpdate: " + point.x + "," + point.y)
            myView?.x = point.x
            myView?.y = point.y
            myView?.invalidate()
        }
        animator1.start()
        var t = 0
        while (t <= 2 * PI) {
            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            t += 1
        }
    }

    private fun requestPermission() {
        val requestPermissionsList: MutableList<String> = ArrayList()
        for (permission in permissionsList) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                            permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(permission)
            }
        }
        if (requestPermissionsList.isNotEmpty()) {
            val requestPermissions = requestPermissionsList.toTypedArray()
            ActivityCompat.requestPermissions(requireActivity(), requestPermissions, REQUEST_CODE)
        } else {
            startPictureSelectionActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(requireContext(), "必须同意所有权限才能使用本功能",
                                Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                startPictureSelectionActivity()
            }
        }
    }

    private fun startPictureSelectionActivity() {
        val intent = Intent()
        intent.setClass(requireContext(), PictureSelectionActivity::class.java)
        startActivity(intent)
    }

    data class Point(val x: Float, val y: Float)

    class PointEvaluator : TypeEvaluator<Point> {
        private var t = 0.0
        override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
            val x = 16 * sin(t / PI).pow(3.0)
            val y = 13 * cos(t) - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t)
            val point = Point(x.toFloat(), y.toFloat())
            t += 0.1
            return point
        }
    }

    private fun onClick(data: MainMenu?) {
        when (data) {
            is MainMenu.AnimateAction -> {
                setScaleAnimation()
                setRotateAnimation()
            }
            is MainMenu.PictureAction -> {
                requestPermission()
            }
            is MainMenu.VideoAction -> {
                findNavController().navigate(R.id.action_mainFragment_to_videoFragment, null, null)
            }
            is MainMenu.DownLoadAction -> {
                val downloadUrl = "http://down.qq.com/qqweb/QQ_1/android_apk/Androidqq_8.4.10.4875_537065980.apk"
                // String downloadUrl = "https://images.pexels.com/photos/4993088/pexels-photo-4993088.jpeg?cs=srgb&dl=pexels-rachel-claire-4993088.jpg&fm=jpg";
                startDownLoad(MyApplication.getInstance(), downloadUrl, object : DownloadListener() {
                    override fun downloadIng(progress: String) {
                        println(progress)
                    }
                }, 3)
            }
            is MainMenu.NotificationAction -> {
                findNavController().navigate(R.id.action_mainFragment_to_notifyFragment)
            }
            is MainMenu.HookAction -> {
                findNavController().navigate(R.id.action_mainFragment_to_hookFragment)
            }
            is MainMenu.UniAppAction -> {
                reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        allGrantedAction = {
                            val appid = "__UNI__0773E11"
                            releaseAndStartApp(appid, "/mnt/sdcard/__UNI__0773E11.wgt")
                        }, deniedAction = {
                    Toast.makeText(requireContext(), "你拒绝了相关权限", Toast.LENGTH_SHORT).show()
                })
            }
            is MainMenu.PluginAction -> {

                Android(requireContext()).apply {
                    this.items(listOf("启动SunFlower插件", "启动自定义插件")) { dialog, index ->
                        dialog.dismiss()

                        val bundle = Bundle()
                        if (index == 0) {
                            //  插件 zip 的路径
                            // bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, "/sdcard/test-plugin.zip")
                            bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, "/data/local/tmp/test-plugin.zip")

                            //启动插件中的对应的 Activity
                            bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, "com.google.samples.apps.sunflower.GardenActivity")

                            // partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
                            bundle.putString(Constant.KEY_PLUGIN_PART_KEY, "sample-plugin")
                        } else if (index == 1) {
                            bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, "/sdcard/plugin-debug.zip")
                            bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, "com.hl.plugin2.MainActivity")
                            bundle.putString(Constant.KEY_PLUGIN_PART_KEY, "my-plugin")
                        }

                        bundle.putBundle(Constant.KEY_EXTRAS, Bundle().apply {
                            this.putString("测试数据", "我是宿主传过来的数据")
                        })

                        startPlugin(bundle)
                    }
                }.show()
            }
        }
    }

    private fun releaseAndStartApp(appid: String, wgtPath: String) {
        uniAppSdk?.releaseWgtToRunPathFromePath(appid, wgtPath) { code, pArgs ->
            println("code = [${code}], pArgs = [${pArgs}]")
            if (code == 1 && uniAppSdk?.isExistsApp(appid) == true) {
                DCUniMPSDK.getInstance().startApp(requireActivity(), appid)
            } else {
                println("isExistsApp = false")
            }
        }
    }

    private fun startPlugin(bundle: Bundle) {
        val pluginManager: PluginManager? = Shadow.getPluginManager()
        /**
         * context context
         * formId  标识本次请求的来源位置，用于区分入口
         * bundle  参数列表, 建议在参数列表加入自己的验证
         * callback 用于从PluginManager实现中返回View
         */
        pluginManager?.enter(requireContext(), Constant.FROM_ID_START_ACTIVITY, bundle, object : EnterCallback {
            override fun onShowLoadingView(view: View?) {}
            override fun onCloseLoadingView() {}
            override fun onEnterComplete() {
                // 启动成功
                Toast.makeText(requireContext(), "启动成功", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
