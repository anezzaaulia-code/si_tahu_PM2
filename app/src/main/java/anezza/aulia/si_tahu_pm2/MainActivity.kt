package anezza.aulia.si_tahu_pm2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import anezza.aulia.si_tahu_pm2.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var b: ActivityMainBinding
    lateinit var produkAdapter: AdapterDataProduk

    private val semuaProduk = mutableListOf<HashMap<String, String>>()
    private val daftarProduk = mutableListOf<HashMap<String, String>>()

    private val urlShow = ApiConfig.PRODUK_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        produkAdapter = AdapterDataProduk(this, daftarProduk)
        b.listProduk.layoutManager = LinearLayoutManager(this)
        b.listProduk.adapter = produkAdapter

        // Filter langsung jalan seperti di web. Tombol hanya untuk reset pencarian.
        b.btnFind.text = "RESET"
        b.btnFind.setOnClickListener {
            b.edCariProduk.setText("")
            filterProduk("")
        }

        b.edCariProduk.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProduk(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        b.btnKeFormTambah.setOnClickListener {
            startActivity(Intent(this, TambahProdukActivity::class.java))
        }

        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stok -> true
                R.id.nav_harga -> {
                    startActivity(Intent(this, HargaActivity::class.java))
                    true
                }
                R.id.nav_parameter -> {
                    startActivity(Intent(this, ParameterActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        showDataProduk()
    }

    fun showDataProduk(nama: String = b.edCariProduk.text.toString().trim()) {
        val request = StringRequest(
            Request.Method.GET,
            urlShow,
            { response ->
                semuaProduk.clear()

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")

                for (x in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(x)
                    val map = HashMap<String, String>()

                    map["id"] = item.optString("id", "")
                    map["kodeProduk"] = item.optString("kodeProduk", "")
                    map["namaProduk"] = item.optString("namaProduk", "")
                    map["jenisProduk"] = item.optString("jenisProduk", "")
                    map["stok"] = item.optString("stokSaatIni", "0")
                    map["satuan"] = item.optString("satuan", "")
                    map["stokMinimum"] = item.optString("stokMinimum", "0")
                    map["aktifDijual"] = item.boolString("aktifDijual")
                    map["tampilDiKasir"] = item.boolString("tampilDiKasir")

                    semuaProduk.add(map)
                }

                filterProduk(nama)
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Toast.makeText(this, "Gagal konek API: $pesan", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun filterProduk(keyword: String) {
        val cari = keyword.trim().lowercase()

        daftarProduk.clear()

        if (cari.isEmpty()) {
            daftarProduk.addAll(semuaProduk)
        } else {
            daftarProduk.addAll(
                semuaProduk.filter { data ->
                    val nama = data["namaProduk"].orEmpty().lowercase()
                    val kode = data["kodeProduk"].orEmpty().lowercase()
                    val jenis = data["jenisProduk"].orEmpty().lowercase()

                    nama.contains(cari) || kode.contains(cari) || jenis.contains(cari)
                }
            )
        }

        produkAdapter.notifyDataSetChanged()
    }
}

private fun JSONObject.boolString(key: String): String {
    return when (val value = opt(key)) {
        is Boolean -> if (value) "1" else "0"
        is Number -> if (value.toInt() != 0) "1" else "0"
        else -> if (optString(key).equals("true", true) || optString(key) == "1") "1" else "0"
    }
}
