package anezza.aulia.si_tahu_pm2

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private val daftarHarga = mutableListOf<HashMap<String, String>>()
    private val urlShow = ApiConfig.HARGA_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityHargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupRecyclerView()
        setupBottomNavigation()
        setupButton()
    }

    override fun onStart() {
        super.onStart()
        showDataHarga()
    }

    private fun setupRecyclerView() {
        hargaAdapter = AdapterDataHarga(this, daftarHarga)
        b.listHarga.layoutManager = LinearLayoutManager(this)
        b.listHarga.adapter = hargaAdapter
    }

    private fun setupBottomNavigation() {
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
                R.id.nav_parameter -> {
                    startActivity(Intent(this, ParameterActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupButton() {
        b.btnTambahHarga.setOnClickListener {
            startActivity(Intent(this, TambahHargaActivity::class.java))
        }
    }

    fun showDataHarga() {
        val request = StringRequest(
            Request.Method.GET,
            urlShow,
            { response ->
                try {
                    Log.d("RESPON_HARGA", response)
                    daftarHarga.clear()

                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val produkObj = item.optJSONObject("produk")

                        val map = HashMap<String, String>()
                        map["id"] = item.optString("id", "")
                        map["idProduk"] = item.optString("idProduk", "")
                        map["namaProduk"] = item.optString(
                            "namaProduk",
                            produkObj?.optString("namaProduk", "").orEmpty()
                        )
                        map["namaHarga"] = item.optString(
                            "namaHarga",
                            item.optString("kanalHarga", "Harga")
                        )
                        map["harga"] = item.optString("hargaSatuan", "0")
                        map["aktif"] = item.boolString("aktif")
                        map["hargaUtama"] = item.boolString("hargaUtama")

                        daftarHarga.add(map)
                    }

                    hargaAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gagal parsing data harga", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val pesanError = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Log.e("ERROR_HARGA", pesanError)
                Toast.makeText(this, pesanError, Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}

private fun JSONObject.boolString(key: String): String {
    return when (val value = opt(key)) {
        is Boolean -> if (value) "1" else "0"
        is Number -> if (value.toInt() != 0) "1" else "0"
        else -> if (optString(key).equals("true", true) || optString(key) == "1") "1" else "0"
    }
}
