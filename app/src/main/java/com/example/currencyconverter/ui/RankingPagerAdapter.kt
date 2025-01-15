package com.example.currencyconverter.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R

class RankingPagerAdapter(
    private val context: Context,
    private val pages: List<List<Map<String, String>>>
) : RecyclerView.Adapter<RankingPagerAdapter.PageViewHolder>() {

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: LinearLayout = itemView.findViewById(R.id.pageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ranking_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = pages[position]
        holder.container.removeAllViews()

        for (item in page) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.ranking_list_item, holder.container, false)
            itemView.findViewById<TextView>(R.id.currencyIcon).text = item["icon"]
            itemView.findViewById<TextView>(R.id.currencyCode).text = item["currency"]
            itemView.findViewById<TextView>(R.id.amount).text = item["amount"]
            itemView.findViewById<TextView>(R.id.secondaryAmount).text = item["secondary"]
            holder.container.addView(itemView)
        }
    }

    override fun getItemCount(): Int = pages.size
}
