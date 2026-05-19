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
import anezza.aulia.si_tahu_pm2.databinding.ItemParameterBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ParameterAdapter(
    private val context: Context,
    private val list: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<ParameterAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemParameterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemParameterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val namaProduk = data["namaProduk"].orEmpty()
        val hasil = data["hasilPerProduksi"].orEmpty()
        val satuan = data["satuanHasil"].orEmpty()
        val aktif = data["aktif"] == "1"

        with(holder.binding) {
            tvInisial.text = if (namaProduk.isNotEmpty()) namaProduk.substring(0, 1).uppercase() else "-"
            tvNamaProduk.text = namaProduk
            tvIdParam.text = "Standar hasil produksi"
            tvHasil.text = "$hasil $satuan"
            badgeAktif.text = if (aktif) "Aktif" else "Nonaktif"
            btnMore.setOnClickListener { showPopup(it, data) }
            root.setOnClickListener { showPopup(it, data) }
        }
    }

    private fun showPopup(view: View, data: HashMap<String, String>) {
        val popup = PopupMenu(context, view)
        popup.menu.add("Edit")
        popup.menu.add("Delete")

        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "Edit" -> {
                    val intent = Intent(context, TambahParameterActivity::class.java)
                    intent.putExtra("MODE", "EDIT")
                    intent.putExtra("id", data["id"])
                    intent.putExtra("idProduk", data["idProduk"])
                    intent.putExtra("namaProduk", data["namaProduk"])
                    intent.putExtra("hasilPerProduksi", data["hasilPerProduksi"])
                    intent.putExtra("satuanHasil", data["satuanHasil"])
                    intent.putExtra("aktif", data["aktif"])
                    intent.putExtra("catatan", data["catatan"])
                    context.startActivity(intent)
                }

                "Delete" -> {
                    AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Yakin hapus data?")
                        .setPositiveButton("Ya") { _, _ -> hapusData(data["id"].toString()) }
                        .setNegativeButton("Tidak", null)
                        .show()
                }
            }
            true
        }

        popup.show()
    }

    private fun hapusData(id: String) {
        val url = "${ApiConfig.PARAMETER_URL}/delete/$id"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            {
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                if (context is ParameterActivity) {
                    context.getData()
                }
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(context, pesan, Toast.LENGTH_LONG).show()
            }
        ) {}

        Volley.newRequestQueue(context).add(request)
    }

    override fun getItemCount(): Int = list.size
}
