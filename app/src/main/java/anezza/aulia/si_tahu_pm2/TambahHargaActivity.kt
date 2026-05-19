package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahHargaBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class TambahHargaActivity : AppCompatActivity() {

    lateinit var b: ActivityTambahHargaBinding

    private val baseUrl = ApiConfig.HARGA_URL
    private val produkUrl = ApiConfig.PRODUK_URL

    private var mode = "TAMBAH"
    private var id = ""
    private var selectedIdProduk = ""
    private val produkNamaToId = linkedMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityTambahHargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        mode = intent.getStringExtra("MODE") ?: "TAMBAH"
        id = intent.getStringExtra("id") ?: ""
        selectedIdProduk = intent.getStringExtra("idProduk") ?: ""

        setupProdukDropdown()
        isiDataEditJikaAda()
        loadProduk()

        b.btnBack.setOnClickListener { finish() }

        b.btnSimpanHarga.setOnClickListener {
            if (validasi()) {
                if (mode == "EDIT") updateData() else simpanDataHarga()
            }
        }
    }

    private fun setupProdukDropdown() {
        b.spinnerProduk.setOnItemClickListener { _, _, position, _ ->
            val namaProduk = b.spinnerProduk.adapter.getItem(position).toString()
            selectedIdProduk = produkNamaToId[namaProduk].orEmpty()
        }
    }

    private fun isiDataEditJikaAda() {
        if (mode != "EDIT") return

        b.spinnerProduk.setText(intent.getStringExtra("namaProduk") ?: "", false)
        b.etNamaHarga.setText(intent.getStringExtra("namaHarga") ?: "")
        b.etHarga.setText(intent.getStringExtra("harga") ?: "")
        b.cbKanalAktif.isChecked = intent.getStringExtra("aktif") == "1"
        b.cbDefaultKasir.isChecked = intent.getStringExtra("hargaUtama") == "1"
        b.btnSimpanHarga.text = "Update Harga"
    }

    private fun loadProduk() {
        val request = StringRequest(
            Request.Method.GET,
            produkUrl,
            { response ->
                try {
                    produkNamaToId.clear()

                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val idProduk = item.optString("id", "")
                        val namaProduk = item.optString("namaProduk", "")

                        if (idProduk.isNotEmpty() && namaProduk.isNotEmpty()) {
                            produkNamaToId[namaProduk] = idProduk
                        }
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        produkNamaToId.keys.toList()
                    )
                    b.spinnerProduk.setAdapter(adapter)

                    if (mode == "EDIT" && selectedIdProduk.isNotEmpty()) {
                        val namaProduk = produkNamaToId.entries
                            .firstOrNull { it.value == selectedIdProduk }
                            ?.key
                            ?: intent.getStringExtra("namaProduk")
                            ?: ""

                        if (namaProduk.isNotEmpty()) {
                            b.spinnerProduk.setText(namaProduk, false)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal membaca data produk", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun validasi(): Boolean {
        if (selectedIdProduk.isEmpty()) {
            val namaProduk = b.spinnerProduk.text.toString().trim()
            selectedIdProduk = produkNamaToId[namaProduk].orEmpty()
        }

        if (selectedIdProduk.isEmpty()) {
            b.spinnerProduk.error = "Produk wajib dipilih"
            b.spinnerProduk.requestFocus()
            return false
        }

        if (b.etNamaHarga.text.toString().trim().isEmpty()) {
            b.etNamaHarga.error = "Nama harga wajib diisi"
            b.etNamaHarga.requestFocus()
            return false
        }

        if (b.etHarga.text.toString().trim().isEmpty()) {
            b.etHarga.error = "Harga wajib diisi"
            b.etHarga.requestFocus()
            return false
        }

        return true
    }

    private fun simpanDataHarga() {
        val request = object : StringRequest(
            Request.Method.POST,
            baseUrl,
            {
                Toast.makeText(this, "Harga berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = paramsHarga()
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateData() {
        if (id.isEmpty()) {
            Toast.makeText(this, "ID harga tidak ditemukan", Toast.LENGTH_LONG).show()
            return
        }

        val urlUpdate = "$baseUrl/update/$id"
        Log.d("UPDATE_URL", urlUpdate)

        val request = object : StringRequest(
            Request.Method.POST,
            urlUpdate,
            {
                Toast.makeText(this, "Harga berhasil diupdate", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = paramsHarga()
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun paramsHarga(): MutableMap<String, String> {
        return hashMapOf(
            "idProduk" to selectedIdProduk,
            "kanalHarga" to "UMUM",
            "namaHarga" to b.etNamaHarga.text.toString().trim(),
            "hargaSatuan" to b.etHarga.text.toString().trim(),
            "aktif" to if (b.cbKanalAktif.isChecked) "1" else "0",
            "hargaUtama" to if (b.cbDefaultKasir.isChecked) "1" else "0"
        )
    }
}
