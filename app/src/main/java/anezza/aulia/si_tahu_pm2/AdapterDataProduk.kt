package anezza.aulia.si_tahu_pm2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anezza.aulia.si_tahu_pm2.databinding.ItemProdukBinding // Pastikan nama bindingnya sesuai dengan file XML kamu

class AdapterDataProduk(
    private val context: Context,
    private val dataList: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<AdapterDataProduk.ViewHolder>() {

    // Gunakan ViewBinding agar lebih aman dan tidak nyasar ID-nya
    inner class ViewHolder(val binding: ItemProdukBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        // Ambil data dari HashMap yang dikirim dari MainActivity
        val nama = data["namaProduk"] ?: "Unknown"
        val stok = data["stok"] ?: "0"
        val satuan = data["satuan"] ?: ""
        val jenis = data["jenisProduk"] ?: "DASAR"

        // Set data ke komponen UI
        with(holder.binding) {
            // 1. Set Inisial (Ambil 1 huruf paling depan dari nama produk, jadikan huruf besar)
            tvInisial.text = if (nama.isNotEmpty()) nama.substring(0, 1).uppercase() else "-"

            // 2. Set Nama Produk
            tvNama.text = nama

            // 3. Set Deskripsi di bawah nama produk
            tvDeskripsi.text = "$jenis • Aktif"

            // 4. Set Nilai Stok di pojok kanan
            tvNilai.text = "$stok $satuan"

            // 5. Ganti teks badge biru jadi "Stok" (opsional, karena sebelumnya "Harga Lainnya")
            badgeTipe.text = "Stok"
        }
    }

    override fun getItemCount(): Int = dataList.size
}