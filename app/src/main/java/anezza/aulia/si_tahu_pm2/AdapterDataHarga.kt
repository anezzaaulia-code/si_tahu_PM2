package anezza.aulia.si_tahu_pm2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
// Pastikan nama binding ini sesuai dengan nama file XML-mu (misal: item_harga.xml -> ItemHargaBinding)
import anezza.aulia.si_tahu_pm2.databinding.ItemHargaBinding

class AdapterDataHarga(
    private val context: Context,
    private val dataList: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<AdapterDataHarga.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHargaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHargaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        // Sesuaikan key ini dengan data JSON dari API Laravel kamu nanti
        val namaKanal = data["namaKanal"] ?: "Unknown"
        val harga = data["harga"] ?: "0"
        val isDefault = data["defaultKasir"] == "1"

        with(holder.binding) {
            // 1. Set Inisial Logo Bulat (Ambil 1 huruf depan)
            tvInisial.text = if (namaKanal.isNotEmpty()) namaKanal.substring(0, 1).uppercase() else "-"

            // 2. Set Nama Kanal (Contoh: "Pasar" atau "Kasir")
            tvNama.text = namaKanal

            // 3. Set Deskripsi
            tvDeskripsi.text = "Harga kanal aktif"

            // 4. Set Format Harga
            tvNilai.text = "Rp$harga"

            // 5. Ganti teks badge biru dinamis
            if (isDefault) {
                badgeTipe.text = "Default Kasir"
                // Kalau mau, kamu bisa set warnanya jadi hijau di sini
            } else {
                badgeTipe.text = "Harga Lainnya"
            }
        }
    }

    override fun getItemCount(): Int = dataList.size
}