package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahProdukBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TambahProdukActivity : AppCompatActivity() {

    lateinit var b: ActivityTambahProdukBinding

    private val baseUrl = ApiConfig.PRODUK_URL

    private var mode = "TAMBAH"
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Spinner Jenis Produk
        val daftarJenis = arrayOf("DASAR", "OLAHAN")

        val adapterJenis = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            daftarJenis
        )

        b.spinnerJenis.setAdapter(adapterJenis)

        // Ambil mode
        mode = intent.getStringExtra("MODE") ?: "TAMBAH"

        // MODE EDIT
        if (mode == "EDIT") {

            id = intent.getStringExtra("id") ?: ""

            Log.d("DEBUG_ID", "ID PRODUK = $id")

            b.etNama.setText(intent.getStringExtra("namaProduk") ?: "")

            b.spinnerJenis.setText(
                intent.getStringExtra("jenisProduk") ?: "",
                false
            )

            b.etSatuan.setText(intent.getStringExtra("satuan") ?: "")

            b.etMinStok.setText(intent.getStringExtra("stokMinimum") ?: "")

            b.cbAktif.isChecked =
                intent.getStringExtra("aktifDijual") == "1"

            b.cbKasir.isChecked =
                intent.getStringExtra("tampilDiKasir") == "1"

            b.btnSimpan.text = "Update Produk"
        }

        // Tombol Back
        b.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Simpan / Update
        b.btnSimpan.setOnClickListener {

            if (validasiInput()) {

                if (mode == "EDIT") {
                    updateData()
                } else {
                    simpanData()
                }
            }
        }
    }

    // VALIDASI
    private fun validasiInput(): Boolean {

        if (b.etNama.text.toString().trim().isEmpty()) {

            b.etNama.error = "Nama produk wajib diisi"
            b.etNama.requestFocus()

            return false
        }

        if (b.spinnerJenis.text.toString().trim().isEmpty()) {

            b.spinnerJenis.error = "Jenis produk wajib dipilih"
            b.spinnerJenis.requestFocus()

            return false
        }

        if (b.etSatuan.text.toString().trim().isEmpty()) {

            b.etSatuan.error = "Satuan wajib diisi"
            b.etSatuan.requestFocus()

            return false
        }

        if (b.etMinStok.text.toString().trim().isEmpty()) {

            b.etMinStok.error = "Stok minimum wajib diisi"
            b.etMinStok.requestFocus()

            return false
        }

        return true
    }

    // INSERT DATA
    private fun simpanData() {

        val request = object : StringRequest(
            Request.Method.POST,
            baseUrl,

            {
                Toast.makeText(
                    this,
                    "Produk berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            { error ->

                val pesanError = try {
                    String(error.networkResponse.data)
                } catch (e: Exception) {
                    error.toString()
                }

                Log.e("INSERT_ERROR", pesanError)

                Toast.makeText(
                    this,
                    pesanError,
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["namaProduk"] =
                    b.etNama.text.toString()

                params["jenisProduk"] =
                    b.spinnerJenis.text.toString()

                params["satuan"] =
                    b.etSatuan.text.toString()

                params["stokMinimum"] =
                    b.etMinStok.text.toString()

                params["aktifDijual"] =
                    if (b.cbAktif.isChecked) "1" else "0"

                params["tampilDiKasir"] =
                    if (b.cbKasir.isChecked) "1" else "0"

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    // UPDATE DATA
    private fun updateData() {

        // CEK ID DULU
        if (id.isEmpty() || id == "null") {

            Toast.makeText(
                this,
                "ID produk tidak ditemukan",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val urlUpdate = "$baseUrl/update/$id"

        Log.d("URL_UPDATE", urlUpdate)

        val request = object : StringRequest(
            Request.Method.POST,
            urlUpdate,

            {
                Toast.makeText(
                    this,
                    "Produk berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            { error ->

                val pesanError = try {
                    String(error.networkResponse.data)
                } catch (e: Exception) {
                    error.toString()
                }

                Log.e("UPDATE_ERROR", pesanError)

                Toast.makeText(
                    this,
                    pesanError,
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["namaProduk"] =
                    b.etNama.text.toString()

                params["jenisProduk"] =
                    b.spinnerJenis.text.toString()

                params["satuan"] =
                    b.etSatuan.text.toString()

                params["stokMinimum"] =
                    b.etMinStok.text.toString()

                // PENTING
                params["aktifDijual"] =
                    if (b.cbAktif.isChecked) "1" else "0"

                params["tampilDiKasir"] =
                    if (b.cbKasir.isChecked) "1" else "0"

                Log.d("PARAM_UPDATE", params.toString())

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}