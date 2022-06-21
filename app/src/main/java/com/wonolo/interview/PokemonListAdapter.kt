package com.wonolo.interview

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*

class PokemonListAdapter : Adapter<PokemonViewHolder>() {
    var data: List<Pokemon> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        return PokemonViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        (holder.itemView as TextView).text = data[position].name.capitalize(Locale.ROOT)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class PokemonViewHolder(context: Context) :
    ViewHolder(AppCompatTextView(ContextThemeWrapper(context, R.style.Pokemon)))
