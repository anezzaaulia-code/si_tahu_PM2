package anezza.aulia.si_tahu_pm2

import android.content.Context
import android.content.Intent
import android.util.Log
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

    inner class ViewHolder(
        val binding: ItemHargaBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemHargaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val data = dataList[position]

        val id =
            data["id"] ?: ""

        val namaKanal =
            data["namaKanal"] ?: "-"

        val harga =
            data["harga"] ?: "0"

        val aktif =
            data["aktif"] ?: "0"

        val defaultKasir =
            data["defaultKasir"] ?: "0"

        val isDefault =
            defaultKasir == "1"

        Log.d(
            "DATA_ADAPTER_HARGA",
            data.toString()
        )

        with(holder.binding) {

            // =========================
            // INISIAL
            // =========================
            tvInisial.text =

                if (namaKanal.isNotEmpty()) {
                    namaKanal.substring(0, 1)
                        .uppercase()
                } else {
                    "-"
                }

            // =========================
            // TEXT
            // =========================
            tvNama.text = namaKanal

            tvDeskripsi.text =

                if (aktif == "1")
                    "Harga kanal aktif"
                else
                    "Harga kanal nonaktif"

            tvNilai.text = "Rp$harga"

            badgeTipe.text =

                if (isDefault)
                    "Default Kasir"
                else
                    "Harga Tambahan"

            // =========================
            // MENU
            // =========================
            btnMore.setOnClickListener {

                showPopupMenu(
                    it,
                    data
                )
            }
        }
    }

    // =========================
    // POPUP MENU
    // =========================
    private fun showPopupMenu(
        view: View,
        data: HashMap<String, String>
    ) {

        val popup =
            PopupMenu(context, view)

        popup.menu.add("Edit")
        popup.menu.add("Delete")

        popup.setOnMenuItemClickListener {

            when (it.title.toString()) {

                // =====================
                // EDIT
                // =====================
                "Edit" -> {

                    val id =
                        data["id"] ?: ""

                    Log.d(
                        "EDIT_ID",
                        id
                    )

                    Toast.makeText(
                        context,
                        "Edit ID = $id",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (id.isEmpty()) {

                        Toast.makeText(
                            context,
                            "ID tidak ditemukan",
                            Toast.LENGTH_LONG
                        ).show()

                        return@setOnMenuItemClickListener true
                    }

                    val intent =
                        Intent(
                            context,
                            TambahHargaActivity::class.java
                        )

                    intent.putExtra(
                        "MODE",
                        "EDIT"
                    )

                    intent.putExtra(
                        "id",
                        id
                    )

                    intent.putExtra(
                        "idProduk",
                        data["idProduk"]
                    )

                    intent.putExtra(
                        "namaKanal",
                        data["namaKanal"]
                    )

                    intent.putExtra(
                        "harga",
                        data["harga"]
                    )

                    intent.putExtra(
                        "aktif",
                        data["aktif"]
                    )

                    intent.putExtra(
                        "defaultKasir",
                        data["defaultKasir"]
                    )

                    context.startActivity(intent)
                }

                // =====================
                // DELETE
                // =====================
                "Delete" -> {

                    val id =
                        data["id"] ?: ""

                    Log.d(
                        "DELETE_ID",
                        id
                    )

                    Toast.makeText(
                        context,
                        "Delete ID = $id",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (id.isEmpty()) {

                        Toast.makeText(
                            context,
                            "ID tidak ditemukan",
                            Toast.LENGTH_LONG
                        ).show()

                        return@setOnMenuItemClickListener true
                    }

                    AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage(
                            "Yakin ingin menghapus data harga?"
                        )

                        .setPositiveButton(
                            "Ya"
                        ) { _, _ ->

                            hapusData(id)
                        }

                        .setNegativeButton(
                            "Tidak",
                            null
                        )

                        .show()
                }
            }

            true
        }

        popup.show()
    }

    // =========================
    // DELETE DATA
    // =========================
    private fun hapusData(id: String) {

        val url =
            "http://192.168.1.22:8000/api/harga-jual/delete/$id"

        Log.d(
            "URL_DELETE",
            url
        )

        val request = object : StringRequest(
            Request.Method.POST,
            url,

            {
                Toast.makeText(
                    context,
                    "Data berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()

                // refresh data
                if (context is HargaActivity) {
                    context.showDataHarga()
                }
            },

            { error ->

                val pesanError = try {

                    val responseBody =
                        String(error.networkResponse.data)

                    Log.e(
                        "DELETE_ERROR",
                        responseBody
                    )

                    responseBody

                } catch (e: Exception) {

                    error.toString()
                }

                Toast.makeText(
                    context,
                    pesanError,
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {}

        Volley.newRequestQueue(context)
            .add(request)
    }

    override fun getItemCount(): Int {

        return dataList.size
    }
}