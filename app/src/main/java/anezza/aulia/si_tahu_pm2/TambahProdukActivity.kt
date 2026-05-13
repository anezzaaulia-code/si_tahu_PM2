package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahProdukBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TambahProdukActivity : AppCompatActivity() {
    lateinit var b: ActivityTambahProdukBinding

    // Pastikan IP ini sama persis dengan yang ada di MainActivity
    val urlStore = "http://192.168.1.22:8000/api/produk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(b.root)

        // 1. MENGHIDUPKAN DROPDOWN (DASAR / OLAHAN)
        val daftarJenis = arrayOf("DASAR", "OLAHAN")
        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, daftarJenis)
        b.spinnerJenis.setAdapter(adapterJenis)
        b.spinnerJenis.setText("DASAR", false)

        // 2. TOMBOL BACK
        b.btnBack.setOnClickListener {
            finish()
        }

        // 3. TOMBOL SIMPAN
        b.btnSimpan.setOnClickListener {
            if (b.etNama.text.toString().trim().isEmpty()) {
                b.etNama.error = "Nama produk wajib diisi!"
                b.etNama.requestFocus()
            } else {
                simpanData()
            }
        }
    }

    fun simpanData() {
        val request = object : StringRequest(Request.Method.POST, urlStore,
            Response.Listener { response ->
                Toast.makeText(this, "Produk Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                finish() // Balik ke halaman daftar produk
            },
            Response.ErrorListener { error ->
                val pesanAsli = error.networkResponse?.let {
                    val dataString = String(it.data, Charsets.UTF_8)
                    Log.e("ERROR_LARAVEL", dataString)
                    "Error ${it.statusCode}: $dataString"
                } ?: "Error Volley: ${error.message}"

                Toast.makeText(this, "Gagal Simpan! Cek Logcat", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                // Pastikan key ini sama dengan database Laravel kamu ya
                return hashMapOf(
                    "namaProduk" to b.etNama.text.toString(),
                    "jenisProduk" to b.spinnerJenis.text.toString(),
                    "satuan" to b.etSatuan.text.toString(),
                    "stokMinimum" to if(b.etMinStok.text.toString().isEmpty()) "0" else b.etMinStok.text.toString(),
                    "stokSaatIni" to "0",
                    "aktifDijual" to if (b.cbAktif.isChecked) "1" else "0",
                    "tampilDiKasir" to if (b.cbKasir.isChecked) "1" else "0"
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Accept" to "application/json")
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}