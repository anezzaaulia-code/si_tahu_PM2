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
import anezza.aulia.si_tahu_pm2.databinding.ItemProdukBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AdapterDataProduk(
    private val context: Context,
    private val dataList: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<AdapterDataProduk.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProdukBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        val nama = data["namaProduk"] ?: "Unknown"
        val stok = data["stok"] ?: "0"
        val satuan = data["satuan"] ?: ""
        val jenis = data["jenisProduk"] ?: "DASAR"

        with(holder.binding) {

            tvInisial.text = if (nama.isNotEmpty()) nama.substring(0, 1).uppercase() else "-"

            tvNama.text = nama

            tvDeskripsi.text = "$jenis • Aktif"

            tvNilai.text = "$stok $satuan"

            badgeTipe.text = "Stok"

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

            when (it.title) {

                "Edit" -> {
                    val intent = Intent(context, TambahProdukActivity::class.java)

                    intent.putExtra("MODE", "EDIT")
                    intent.putExtra("id", data["id"])
                    intent.putExtra("namaProduk", data["namaProduk"])
                    intent.putExtra("jenisProduk", data["jenisProduk"])
                    intent.putExtra("satuan", data["satuan"])
                    intent.putExtra("stokMinimum", data["stokMinimum"])
                    intent.putExtra("aktifDijual", data["aktifDijual"])
                    intent.putExtra("tampilDiKasir", data["tampilDiKasir"])

                    context.startActivity(intent)
                }

                "Delete" -> {
                    AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Yakin ingin menghapus produk ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            hapusData(data["id"].toString())
                        }
                        .setNegativeButton("Tidak", null)
                        .show()
                }
            }

            true
        }

        popup.show()
    }

    private fun hapusData(id: String) {

        val url = "http://192.168.1.22:8000/api/produk/delete/$id"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            {
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()

                if (context is MainActivity) {
                    context.showDataProduk("")
                }
            },
            {
                Toast.makeText(context, "Gagal hapus data", Toast.LENGTH_SHORT).show()
            }
        ) {}

        Volley.newRequestQueue(context).add(request)
    }

    override fun getItemCount(): Int = dataList.size
}