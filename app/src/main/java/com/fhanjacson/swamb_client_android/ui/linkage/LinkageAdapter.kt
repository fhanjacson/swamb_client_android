package com.fhanjacson.swamb_client_android.ui.linkage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.model.LinkageData
import java.util.*
import kotlin.collections.ArrayList

class LinkageAdapter(private val linkageList: ArrayList<LinkageData>, private val fragment: Fragment, private val callback: LinkageClickListener): RecyclerView.Adapter<LinkageAdapter.LinkageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkageViewHolder {
        val linkageViewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_linkage, parent, false)
        return LinkageViewHolder(linkageViewHolder)
    }

    override fun getItemCount(): Int {
        return linkageList.size
    }

    override fun onBindViewHolder(holder: LinkageViewHolder, position: Int) {
        val linkageData = linkageList[position]
        holder.bind(linkageData, fragment, callback)
    }

    class LinkageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var linkageNicknameTextView: TextView = itemView.findViewById(R.id.linkageNickname_text)
        private var vendorImageView: ImageView = itemView.findViewById(R.id.vendorIcon_image)
        private var vendorNameTextview: TextView = itemView.findViewById(R.id.vendorName_text)
        private var vendorUserIDTextview: TextView = itemView.findViewById(R.id.vendorUserID_text)
        private var lastAuthTimestampTextview: TextView = itemView.findViewById(R.id.lastAuthTimestamp_text)
        private var linkageCreatedTimestampTextview: TextView = itemView.findViewById(R.id.linkageCreatedTimestamp_text)

        fun bind(linkageData: LinkageData, fragment: Fragment, callback: LinkageClickListener) {
            val lastAuthDate = Date(linkageData.lastAuthTimestamp)
            val linkageCreatedDate = Date(linkageData.linkageCreatedTimestamp)

            linkageNicknameTextView.text = linkageData.linkageNickname
            vendorNameTextview.text = linkageData.vendorName
            vendorUserIDTextview.text = linkageData.vendorUserID
            lastAuthTimestampTextview.text = Constant.SIMPLE_DATE_FORMAT.format(lastAuthDate)
            linkageCreatedTimestampTextview.text = Constant.SIMPLE_DATE_FORMAT.format(linkageCreatedDate)
            Glide.with(fragment).load(linkageData.vendorIconUrl).centerCrop().into(vendorImageView)

            itemView.setOnClickListener {
                callback.onItemViewClick(linkageData)
            }
        }
    }

    interface LinkageClickListener {
        fun onItemViewClick(linkage: LinkageData)
    }

}