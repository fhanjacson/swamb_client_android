package com.fhanjacson.swamb_client_android.ui.linkage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.model.LinkageData

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

        var vendorImageView = itemView.findViewById<ImageView>(R.id.vendorIcon_image)
        var vendorNameTextview = itemView.findViewById<TextView>(R.id.vendorName_text)
        var vendorUserIDTextview = itemView.findViewById<TextView>(R.id.vendorUserID_text)
        var lastAuthTimestampTextview = itemView.findViewById<TextView>(R.id.lastAuthTimestamp_text)
        var linkageCreatedTimestampTextview = itemView.findViewById<TextView>(R.id.linkageCreatedTimestamp_text)

        fun bind(linkageData: LinkageData, fragment: Fragment, callback: LinkageClickListener) {
            vendorNameTextview.text = linkageData.vendorName
            vendorUserIDTextview.text = linkageData.vendorUserID
            lastAuthTimestampTextview.text = linkageData.lastAuthTimestamp
            linkageCreatedTimestampTextview.text = linkageData.linkageCreatedTimestamp
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