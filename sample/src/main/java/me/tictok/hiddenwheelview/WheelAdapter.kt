package me.tictok.hiddenwheelview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class WheelAdapter(stringArray: Array<String>, itemLayoutId: Int) : RecyclerView.Adapter<WheelAdapter.WheelViewHolder>() {

    private val mStringArray: Array<String> = stringArray
    private val mItemLayoutId: Int = itemLayoutId
    private lateinit var context: Context

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    class WheelViewHolder(listItem: LinearLayout) : RecyclerView.ViewHolder(listItem) {
        val mListItem: LinearLayout = listItem
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WheelViewHolder {
        val listItem = LayoutInflater.from(p0.context).inflate(mItemLayoutId, p0, false) as LinearLayout
        context = p0.context
        return WheelViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: WheelViewHolder, i: Int) {
        holder.mListItem.findViewById<TextView>(R.id.textView).text = mStringArray[i]
        if (mOnItemClickListener != null) {
            holder.mListItem.setOnClickListener {
                mOnItemClickListener!!.onItemClick(holder.mListItem, holder.adapterPosition)
            }
            holder.mListItem.setOnLongClickListener(View.OnLongClickListener {
                mOnItemClickListener!!.onItemLongClick(holder.mListItem, holder.adapterPosition)
                false
            })
        }
    }

    override fun getItemCount(): Int = mStringArray.size
}