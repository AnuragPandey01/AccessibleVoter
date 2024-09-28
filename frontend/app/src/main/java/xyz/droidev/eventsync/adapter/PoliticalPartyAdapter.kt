package xyz.droidev.eventsync.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import xyz.droidev.eventsync.R
import xyz.droidev.eventsync.data.api.PoliticalParty
import xyz.droidev.eventsync.databinding.ItemPoliticalPartyBinding

class PoliticalPartyAdapter(
    private var parties: List<PoliticalParty>
) : Adapter<PoliticalPartyAdapter.ViewHolder>(){

    var selectedParty : Int?= null
    private var onItemClick: (PoliticalParty) -> Unit = {}
    inner class ViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = ItemPoliticalPartyBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_political_party,parent,false))
    }

    override fun getItemCount() = parties.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curr = parties[position]
        holder.binding.apply {
            partyName.text = curr.party_name
            leaderName.text = curr.leader
            Glide.with(this.root.context)
                .load("https://api.dicebear.com/9.x/personas/png?seed=${curr.leader}")
                .into(partyIcon)
            isSelected.isChecked = (selectedParty == position)
            isSelected.isSelected = (selectedParty == position)

            root.setOnClickListener {
                val t = selectedParty
                selectedParty = position
                t?.let{ notifyItemChanged(t) }
                notifyItemChanged(selectedParty!!)
                onItemClick(curr)
            }
        }
    }

    fun setData(teams: List<PoliticalParty>){
        this.parties = teams
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClick: (PoliticalParty) -> Unit){
        this.onItemClick = onItemClick
    }

}