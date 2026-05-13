package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahHargaBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TambahHargaActivity : AppCompatActivity() {

    lateinit var b: ActivityTambahHargaBinding

    private val baseUrl =
        "http://192.168.1.22:8000/api/harga-jual"

    private var mode = "TAMBAH"
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityTambahHargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        mode = intent.getStringExtra("MODE") ?: "TAMBAH"

        if (mode == "EDIT") {

            id = intent.getStringExtra("id") ?: ""

            b.etNamaProduk.setText(
                intent.getStringExtra("idProduk") ?: ""
            )

            b.etNamaKanal.setText(
                intent.getStringExtra("namaKanal") ?: ""
            )

            b.etHarga.setText(
                intent.getStringExtra("harga") ?: ""
            )

            b.cbKanalAktif.isChecked =
                intent.getStringExtra("aktif") == "1"

            b.cbDefaultKasir.isChecked =
                intent.getStringExtra("defaultKasir") == "1"

            b.btnSimpanHarga.text = "Update Harga"
        }

        b.btnBack.setOnClickListener {
            finish()
        }

        b.btnSimpanHarga.setOnClickListener {

            if (mode == "EDIT") {
                updateData()
            } else {
                simpanDataHarga()
            }
        }
    }

    private fun simpanDataHarga() {

        val request = object : StringRequest(
            Request.Method.POST,
            baseUrl,

            {
                Toast.makeText(
                    this,
                    "Harga berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            {
                Toast.makeText(
                    this,
                    "Gagal simpan data",
                    Toast.LENGTH_SHORT
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf(

                    "idProduk" to
                            b.etNamaProduk.text.toString(),

                    "kanalHarga" to
                            b.etNamaKanal.text.toString(),

                    "namaHarga" to
                            "Harga Baru",

                    "hargaSatuan" to
                            b.etHarga.text.toString(),

                    "aktif" to
                            if (b.cbKanalAktif.isChecked) "1" else "0",

                    "hargaUtama" to
                            if (b.cbDefaultKasir.isChecked) "1" else "0"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateData() {

        val urlUpdate = "$baseUrl/update/$id"

        Log.d("UPDATE_URL", urlUpdate)

        val request = object : StringRequest(
            Request.Method.POST,
            urlUpdate,

            {
                Toast.makeText(
                    this,
                    "Harga berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            { error ->

                val pesan = try {
                    String(error.networkResponse.data)
                } catch (e: Exception) {
                    error.toString()
                }

                Toast.makeText(
                    this,
                    pesan,
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf(

                    "idProduk" to
                            b.etNamaProduk.text.toString(),

                    "kanalHarga" to
                            b.etNamaKanal.text.toString(),

                    "namaHarga" to
                            "Harga Update",

                    "hargaSatuan" to
                            b.etHarga.text.toString(),

                    "aktif" to
                            if (b.cbKanalAktif.isChecked) "1" else "0",

                    "hargaUtama" to
                            if (b.cbDefaultKasir.isChecked) "1" else "0"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}