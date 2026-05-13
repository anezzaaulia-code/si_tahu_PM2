package anezza.aulia.si_tahu_pm2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anezza.aulia.si_tahu_pm2.databinding.ItemParameterBinding

class ParameterAdapter(private val list: ArrayList<HashMap<String, String>>) :
    RecyclerView.Adapter<ParameterAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemParameterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemParameterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.binding.tvNamaProduk.text = data["namaProduk"]
        holder.binding.tvIdParam.text = "${data["id"]} • Standar hasil..."

        // Gabungkan hasil produksi dengan satuannya (Contoh: 100 pcs)
        val hasil = data["hasilPerProduksi"]
        val satuan = data["satuanHasil"]
        holder.binding.tvHasil.text = "$hasil $satuan"
    }

    override fun getItemCount(): Int = list.size
}