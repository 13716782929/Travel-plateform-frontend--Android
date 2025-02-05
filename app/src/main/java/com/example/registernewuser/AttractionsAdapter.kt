package com.example.registernewuser

class AttractionsAdapter(
    private val attractions: List<Attraction>,
    private val onItemClick: (Attraction) -> Unit
) : RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder>() {

    inner class AttractionViewHolder(private val binding: ItemAttractionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attraction: Attraction) {
            binding.attractionName.text = attraction.name
            binding.attractionLocation.text = attraction.location
            binding.attractionRating.text = attraction.rating.toString()
            binding.attractionPrice.text = attraction.price
            binding.attractionImage.setImageResource(attraction.imageResId)

            binding.root.setOnClickListener { onItemClick(attraction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val binding = ItemAttractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(attractions[position])
    }

    override fun getItemCount() = attractions.size
}
