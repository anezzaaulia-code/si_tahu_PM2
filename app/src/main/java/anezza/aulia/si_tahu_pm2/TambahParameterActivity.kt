package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahParameterBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class TambahParameterActivity : AppCompatActivity() {

    private lateinit var b: ActivityTambahParameterBinding

    private val baseUrl = ApiConfig.PARAMETER_URL
    private val produkUrl = ApiConfig.PRODUK_URL

    private var mode = "TAMBAH"
    private var id = ""
    private var selectedIdProduk = ""
    private val produkNamaToId = linkedMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityTambahParameterBinding.inflate(layoutInflater)
        setContentView(b.root)

        mode = intent.getStringExtra("MODE") ?: "TAMBAH"
        id = intent.getStringExtra("id") ?: ""
        selectedIdProduk = intent.getStringExtra("idProduk") ?: ""

        setupProdukDropdown()
        isiDataEditJikaAda()
        loadProdukDasar()

        b.btnBack.setOnClickListener { finish() }

        b.btnSimpanParameter.setOnClickListener {
            if (validasi()) {
                if (mode == "EDIT") updateData() else simpanData()
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
        b.etHasilMasak.setText(intent.getStringExtra("hasilPerProduksi") ?: "")
        b.etSatuanHasil.setText(intent.getStringExtra("satuanHasil") ?: "")
        b.etCatatan.setText(intent.getStringExtra("catatan") ?: "")
        b.cbAktif.isChecked = intent.getStringExtra("aktif") == "1"
        b.btnSimpanParameter.text = "Update Parameter"
    }

    private fun loadProdukDasar() {
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
                        val jenisProduk = item.optString("jenisProduk", "")

                        if (jenisProduk == "DASAR") {
                            val idProduk = item.optString("id", "")
                            val namaProduk = item.optString("namaProduk", "")

                            if (idProduk.isNotEmpty() && namaProduk.isNotEmpty()) {
                                produkNamaToId[namaProduk] = idProduk
                            }
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
            b.spinnerProduk.error = "Produk dasar wajib dipilih"
            b.spinnerProduk.requestFocus()
            return false
        }

        if (b.etHasilMasak.text.toString().trim().isEmpty()) {
            b.etHasilMasak.error = "Hasil produksi wajib diisi"
            b.etHasilMasak.requestFocus()
            return false
        }

        if (b.etSatuanHasil.text.toString().trim().isEmpty()) {
            b.etSatuanHasil.error = "Satuan wajib diisi"
            b.etSatuanHasil.requestFocus()
            return false
        }

        return true
    }

    private fun simpanData() {
        val request = object : StringRequest(
            Request.Method.POST,
            baseUrl,
            {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = paramsParameter()
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateData() {
        if (id.isEmpty()) {
            Toast.makeText(this, "ID parameter tidak ditemukan", Toast.LENGTH_LONG).show()
            return
        }

        val urlUpdate = "$baseUrl/update/$id"

        val request = object : StringRequest(
            Request.Method.POST,
            urlUpdate,
            {
                Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = paramsParameter()
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun paramsParameter(): MutableMap<String, String> {
        return hashMapOf(
            "idProduk" to selectedIdProduk,
            "hasilPerProduksi" to b.etHasilMasak.text.toString().trim(),
            "satuanHasil" to b.etSatuanHasil.text.toString().trim(),
            "catatan" to b.etCatatan.text.toString().trim(),
            "aktif" to if (b.cbAktif.isChecked) "1" else "0"
        )
    }
}
