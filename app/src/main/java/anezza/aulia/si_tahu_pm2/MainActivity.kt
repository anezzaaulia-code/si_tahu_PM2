package anezza.aulia.si_tahu_pm2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import anezza.aulia.si_tahu_pm2.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var b: ActivityMainBinding
    lateinit var produkAdapter: AdapterDataProduk
    var daftarProduk = mutableListOf<HashMap<String, String>>()

    // Gunakan IP Laptop kamu agar bisa diakses dari HP/Emulator
    val urlShow = "http://192.168.1.22:8000/api/produk"

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

        // 1. Setup RecyclerView
        produkAdapter = AdapterDataProduk(this, daftarProduk)
        b.listProduk.layoutManager = LinearLayoutManager(this)
        b.listProduk.adapter = produkAdapter

        // 2. Tombol Cari
        b.btnFind.setOnClickListener {
            showDataProduk(b.edCariProduk.text.toString().trim())
        }

        // 3. Tombol Tambah (FAB)
        b.btnKeFormTambah.setOnClickListener {
            val intent = Intent(this, TambahProdukActivity::class.java)
            startActivity(intent)
        }

        // 4. Navigasi Bawah
        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stok -> {
                    // Kita sudah di halaman stok, jadi tidak perlu pindah
                    true
                }
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
        showDataProduk("")
    }

    fun showDataProduk(nama: String) {
        val request = StringRequest(Request.Method.GET, urlShow,
            { response ->
                daftarProduk.clear()
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")

                for (x in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(x)
                    val map = HashMap<String, String>()
                    map["kodeProduk"] = item.getString("kodeProduk")
                    map["namaProduk"] = item.getString("namaProduk")
                    map["jenisProduk"] = item.getString("jenisProduk")
                    map["stok"] = item.getString("stokSaatIni")
                    map["satuan"] = item.getString("satuan")
                    daftarProduk.add(map)
                }
                produkAdapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Gagal konek API: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}