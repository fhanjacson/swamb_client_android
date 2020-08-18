package com.fhanjacson.swamb_client_android.ui.linkage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.model.AuthenticationHistoryResponse
import java.util.*
import kotlin.collections.ArrayList

class AuthHistoryAdapter(private val authHistory: ArrayList<AuthenticationHistoryResponse.AuthenticationHistory>): RecyclerView.Adapter<AuthHistoryAdapter.AuthHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthHistoryViewHolder {
        val authHistoryViewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_auth_history, parent, false)
        return AuthHistoryViewHolder(authHistoryViewHolder)
    }

    override fun getItemCount(): Int {
        return authHistory.size
    }

    override fun onBindViewHolder(holder: AuthHistoryViewHolder, position: Int) {
        holder.bind(authHistory[position])
    }

    class AuthHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var authTimestamp: TextView = itemView.findViewById(R.id.authTimestamp)

        fun bind(authHistory: AuthenticationHistoryResponse.AuthenticationHistory) {
            val authTimestampDate = Date(authHistory.authTimestamp)
            authTimestamp.text = Constant.SIMPLE_DATE_FORMAT.format(authTimestampDate)
        }
    }
}