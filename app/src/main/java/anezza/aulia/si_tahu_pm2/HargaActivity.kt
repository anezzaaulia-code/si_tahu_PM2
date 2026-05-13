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

    private var daftarHarga =
        mutableListOf<HashMap<String, String>>()

    private val urlShow =
        "http://192.168.1.22:8000/api/harga-jual"

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

    // =========================
    // SETUP RECYCLER VIEW
    // =========================
    private fun setupRecyclerView() {

        hargaAdapter =
            AdapterDataHarga(this, daftarHarga)

        b.listHarga.layoutManager =
            LinearLayoutManager(this)

        b.listHarga.adapter = hargaAdapter
    }

    // =========================
    // BOTTOM NAVIGATION
    // =========================
    private fun setupBottomNavigation() {

        b.bottomNav.selectedItemId = R.id.nav_harga

        b.bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_stok -> {

                    val intent =
                        Intent(this, MainActivity::class.java)

                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(intent)

                    true
                }

                R.id.nav_harga -> true

                else -> false
            }
        }
    }

    // =========================
    // BUTTON
    // =========================
    private fun setupButton() {

        b.btnTambahHarga.setOnClickListener {

            val intent =
                Intent(this, TambahHargaActivity::class.java)

            startActivity(intent)
        }
    }

    // =========================
    // SHOW DATA
    // =========================
    fun showDataHarga() {

        val request = StringRequest(
            Request.Method.GET,
            urlShow,

            { response ->

                try {

                    Log.d("RESPON_HARGA", response)

                    daftarHarga.clear()

                    val jsonObject =
                        JSONObject(response)

                    val jsonArray =
                        jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {

                        val item =
                            jsonArray.getJSONObject(i)

                        val map =
                            HashMap<String, String>()

                        // =====================
                        // AMBIL DATA JSON
                        // =====================

                        map["id"] =
                            item.optString("id", "")

                        map["idProduk"] =
                            item.optString("idProduk", "")

                        map["namaKanal"] =
                            item.optString("kanalHarga", "")

                        map["harga"] =
                            item.optString("hargaSatuan", "0")

                        map["aktif"] =
                            item.optString("aktif", "0")

                        // PENTING
                        // Samakan dengan field Laravel
                        map["defaultKasir"] =
                            item.optString("defaultKasir", "0")

                        Log.d(
                            "DATA_HARGA",
                            map.toString()
                        )

                        daftarHarga.add(map)
                    }

                    hargaAdapter.notifyDataSetChanged()

                } catch (e: Exception) {

                    e.printStackTrace()

                    Toast.makeText(
                        this,
                        "Gagal parsing data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },

            { error ->

                val pesanError = try {

                    val responseBody =
                        String(error.networkResponse.data)

                    Log.e(
                        "ERROR_HARGA",
                        responseBody
                    )

                    responseBody

                } catch (e: Exception) {

                    error.toString()
                }

                Toast.makeText(
                    this,
                    pesanError,
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this)
            .add(request)
    }
}