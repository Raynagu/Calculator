package com.mendtrix.calculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_view.view.*

class InputListAdapter(val ctx:Context, val resource:Int, val list:List<String>) : ArrayAdapter<String>(ctx, resource, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)
        val item = list[position]

        if(item.first() == '='){
            view.tv_item_total.text = item
            view.tv_item_total.visibility = View.VISIBLE
            view.tv_item.visibility = View.GONE
        }else{
            view.tv_item.text = item
            view.tv_item.visibility = View.VISIBLE
            view.tv_item_total.visibility = View.GONE
        }
        return view
    }
}