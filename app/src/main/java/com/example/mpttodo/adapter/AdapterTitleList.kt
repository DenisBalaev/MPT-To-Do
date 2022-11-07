package com.example.mpttodo.adapter

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpttodo.R
import com.example.mpttodo.model.ListCreate

class AdapterTitleList(
    private val context: Context,
    private val nameListTitle:List<ListCreate>,
    val listener:(ListCreate)->Unit
):RecyclerView.Adapter<AdapterTitleList.TitleViewHolder>(){

    class TitleViewHolder(view: View): RecyclerView.ViewHolder(view){
        val titleText = view.findViewById<TextView>(R.id.title)
        val imag = view.findViewById<ImageView>(R.id.images)

        fun bindView(title:ListCreate,listener: (ListCreate) -> Unit){
            if (title.nameList == "Завершенные") {
                imag.setImageResource(R.drawable.ic_check)
            }
            titleText.text =title.nameList
            imag.setColorFilter(title.color);
            itemView.setOnClickListener {
               listener(title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder =
        TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_title,parent,false))

    override fun getItemCount(): Int = nameListTitle.size

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        holder.bindView(nameListTitle[position], listener)
    }

}
