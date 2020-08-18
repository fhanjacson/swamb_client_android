package com.fhanjacson.swamb_client_android.ui.authLog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.model.AuthenticationLogResponse
import java.util.*
import kotlin.collections.ArrayList

class AuthLogAdapter(private val authLog: ArrayList<AuthenticationLogResponse.AuthenticationLog>): RecyclerView.Adapter<AuthLogAdapter.AuthLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthLogViewHolder {
        val authLogViewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_auth_log, parent, false)
        return AuthLogViewHolder(authLogViewHolder)
    }

    override fun getItemCount(): Int {
        return authLog.size
    }

    override fun onBindViewHolder(holder: AuthLogViewHolder, position: Int) {
        holder.bind(authLog[position])
    }

    class AuthLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var authTimestamp: TextView = itemView.findViewById(R.id.authLogTimestamp)
        private var authIP: TextView = itemView.findViewById(R.id.authLogIP)
        private var authBrowser: TextView = itemView.findViewById(R.id.authLogBrowser)
        private var authMachine: TextView = itemView.findViewById(R.id.authLogMachine)
        private var authVendorName: TextView = itemView.findViewById(R.id.authLogVendorName)
        private var authVendorUserID: TextView = itemView.findViewById(R.id.authLogVendorUserID)

        fun bind(authHistory: AuthenticationLogResponse.AuthenticationLog) {
            val authTimestampDate = Date(authHistory.authTimestamp)
            authTimestamp.text = Constant.SIMPLE_DATE_FORMAT.format(authTimestampDate)
            authIP.text = authHistory.ip
            authBrowser.text = "${authHistory.browser} ${authHistory.version}"
            authMachine.text = "${authHistory.platform} ${authHistory.os}"
            authVendorName.text = authHistory.vendorName
            authVendorUserID.text = authHistory.vendorUserID
        }
    }
}