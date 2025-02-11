package com.example.zhanglei.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author  张磊  on  2020/11/16 at 12:33
 * Email: 913305160@qq.com
 */
abstract class BaseAbstractAdapter<T>(private var data: List<T>) : RecyclerView.Adapter<BaseAbstractAdapter<T>.ViewHolder>() {

    open var headerView: View? = null
    open var footerView: View? = null

    abstract val itemLayout: Int

    open var onViewHolderInitListener: (viewHolder: ViewHolder, position: Int, data: T?) -> Unit = { _, _, _ -> }
    open var onBindItemListener: (viewHolder: ViewHolder, data: T?) -> Unit = { _, _ -> }

    inner class ViewHolder(itemView: View, private val viewType: Int) : RecyclerView.ViewHolder(itemView) {

        /**
         * 该属性需要在数据改变时重新设置 ViewHolder 对应的数据
         * onBindViewHolder（）中默认已实现，若想自己设置请确保数据正确，否则对应回调方法的参数也会不正确
         */
        var initData: T? = null
            set(value) {
                //重新设置 ViewHolder对应的数据时，同时更新监听方法的参数
                onViewHolderInitListener(this, viewType, value)
                field = value
            }

        init {
            initData = getDataForHeaderAndFooter(viewType)
        }

        fun <V : View> getView(viewId: Int): V? {
            return itemView.findViewById(viewId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        if (viewType == 0) {
            itemView = headerView ?: itemView
        } else if (viewType == itemCount - 1) {
            itemView = footerView ?: itemView
        }
        return ViewHolder(itemView, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getDataForHeaderAndFooter(position)

        if (holder.initData != data) {
            holder.initData = data
        }
        onBindItemListener(holder, data)
    }

    /**
     * 获取头尾存在或不存在时对应位置的数据
     */
    private fun getDataForHeaderAndFooter(position: Int): T? {
        //头尾布局都存在时
        return if (headerView != null && footerView != null) {
            if (position == 0 || position == itemCount - 1) {
                //头尾ViewHolder处理
                null
            } else {
                //正常的ViewHolder处理
                data[position - 1]
            }
        } else {
            if (headerView == null && footerView == null) {
                //头尾布局都不存在时的处理
                data[position]
            } else if (headerView != null) {
                //仅头布局存在时
                if (position == 0) {
                    null
                } else {
                    data[position - 1]
                }
            } else if (footerView != null) {
                //仅尾布局存在时
                if (position < itemCount - 1) {
                    data[position]
                } else {
                    null
                }
            } else null
        }
    }

    override fun getItemCount(): Int {
        return data.size + if (headerView == null) 0 else 1 + if (footerView == null) 0 else 1
    }

    fun updateData(newData: List<T>) {
        this.data = newData
        notifyDataSetChanged()
    }


}