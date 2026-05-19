package anezza.aulia.si_tahu_pm2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import anezza.aulia.si_tahu_pm2.databinding.ItemHargaBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

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

        val namaHarga = data["namaHarga"] ?: "Harga"
        val namaProduk = data["namaProduk"] ?: "-"
        val harga = data["harga"] ?: "0"
        val aktif = data["aktif"] == "1"
        val isDefault = data["hargaUtama"] == "1"

        with(holder.binding) {
            tvInisial.text = if (namaHarga.isNotEmpty()) namaHarga.substring(0, 1).uppercase() else "-"
            tvNama.text = namaHarga
            tvDeskripsi.text = "$namaProduk • ${if (aktif) "Aktif" else "Nonaktif"}"
            tvNilai.text = "Rp$harga"
            badgeAktif.text = if (aktif) "Aktif" else "Nonaktif"
            badgeTipe.text = if (isDefault) "Default Kasir" else "Harga Lainnya"

            btnMore.setOnClickListener {
                showPopupMenu(it, data)
            }
        }
    }

    private fun showPopupMenu(view: View, data: HashMap<String, String>) {
        val popup = PopupMenu(context, view)
        popup.menu.add("Edit")
        popup.menu.add("Delete")

        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "Edit" -> {
                    val id = data["id"] ?: ""
                    if (id.isEmpty()) {
                        Toast.makeText(context, "ID tidak ditemukan", Toast.LENGTH_LONG).show()
                        return@setOnMenuItemClickListener true
                    }

                    val intent = Intent(context, TambahHargaActivity::class.java)
                    intent.putExtra("MODE", "EDIT")
                    intent.putExtra("id", id)
                    intent.putExtra("idProduk", data["idProduk"])
                    intent.putExtra("namaProduk", data["namaProduk"])
                    intent.putExtra("namaHarga", data["namaHarga"])
                    intent.putExtra("harga", data["harga"])
                    intent.putExtra("aktif", data["aktif"])
                    intent.putExtra("hargaUtama", data["hargaUtama"])
                    context.startActivity(intent)
                }

                "Delete" -> {
                    val id = data["id"] ?: ""
                    if (id.isEmpty()) {
                        Toast.makeText(context, "ID tidak ditemukan", Toast.LENGTH_LONG).show()
                        return@setOnMenuItemClickListener true
                    }

                    AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Yakin ingin menghapus data harga?")
                        .setPositiveButton("Ya") { _, _ -> hapusData(id) }
                        .setNegativeButton("Tidak", null)
                        .show()
                }
            }
            true
        }

        popup.show()
    }

    private fun hapusData(id: String) {
        val url = "${ApiConfig.HARGA_URL}/delete/$id"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            {
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                if (context is HargaActivity) {
                    context.showDataHarga()
                }
            },
            { error ->
                val pesanError = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(context, pesanError, Toast.LENGTH_LONG).show()
            }
        ) {}

        Volley.newRequestQueue(context).add(request)
    }

    override fun getItemCount(): Int = dataList.size
}
