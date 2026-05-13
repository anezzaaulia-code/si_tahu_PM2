package anezza.aulia.si_tahu_pm2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import anezza.aulia.si_tahu_pm2.databinding.ActivityHargaBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class HargaActivity : AppCompatActivity() {
    lateinit var b: ActivityHargaBinding
    lateinit var hargaAdapter: AdapterDataHarga
    var daftarHarga = mutableListOf<HashMap<String, String>>()

    val urlShow = "http://192.168.1.22:8000/api/harga-jual"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        // 1. Setup RecyclerView
        hargaAdapter = AdapterDataHarga(this, daftarHarga)
        b.listHarga.layoutManager = LinearLayoutManager(this)
        b.listHarga.adapter = hargaAdapter

        // 2. Navigasi Bawah (Lebih ringkas pakai Lambda)
        b.bottomNav.selectedItemId = R.id.nav_harga
        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stok -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_harga -> true
                else -> false
            }
        }

        // 3. Tombol Tambah Harga (Listener langsung)
        b.btnTambahHarga.setOnClickListener {
            val intent = Intent(this, TambahHargaActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Tombol tambah diklik!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        showDataHarga()
    }

    fun showDataHarga() {
        // Volley ala Kotlin: Langsung pakai kurung kurawal { response -> } tanpa Response.Listener
        val request = StringRequest(Request.Method.GET, urlShow,
            { response ->
                daftarHarga.clear()
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")

                for (x in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(x)

                    // Gaya Normal Kotlin: Langsung bungkus jadi hashMapOf, nggak perlu .put() berulang kali
                    daftarHarga.add(hashMapOf(
                        "namaKanal" to item.getString("kanalHarga"),
                        "harga" to item.getString("hargaSatuan"),
                        // Pakai optString biar nggak crash kalau di Laravel-nya lagi nggak ngirim defaultKasir
                        "defaultKasir" to item.optString("defaultKasir", "0")
                    ))
                }
                hargaAdapter.notifyDataSetChanged()
            },
            { error ->
                // Handle error elegan, cek status code kalau ada
                val pesanError = error.networkResponse?.let {
                    "Error server: Code ${it.statusCode}"
                } ?: "Error Volley: ${error.message}"

                Toast.makeText(this, pesanError, Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}