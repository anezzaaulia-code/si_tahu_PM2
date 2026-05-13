package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahHargaBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TambahHargaActivity : AppCompatActivity() {
    lateinit var b: ActivityTambahHargaBinding

    // IP Laravel-mu untuk Harga Jual
    val urlStore = "http://192.168.1.22:8000/api/harga-jual"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahHargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Tombol Kembali
        b.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Simpan
        b.btnSimpanHarga.setOnClickListener {
            // Validasi biar nggak ngirim data kosong
            if (b.etNamaKanal.text.toString().trim().isEmpty()) {
                b.etNamaKanal.error = "Nama Kanal wajib diisi!"
                b.etNamaKanal.requestFocus()
            } else if (b.etHarga.text.toString().trim().isEmpty()) {
                b.etHarga.error = "Harga wajib diisi!"
                b.etHarga.requestFocus()
            } else {
                simpanDataHarga()
            }
        }
    }

    fun simpanDataHarga() {
        val request = object : StringRequest(Request.Method.POST, urlStore,
            Response.Listener<String> { response ->
                Toast.makeText(this@TambahHargaActivity, "Harga Kanal Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                finish() // Balik ke halaman daftar harga
            },
            Response.ErrorListener { error: VolleyError ->
                val pesanAsli = error.networkResponse?.let {
                    val dataString = String(it.data, Charsets.UTF_8)
                    Log.e("ERROR_LARAVEL", dataString)
                    "Error ${it.statusCode}: $dataString"
                } ?: "Error Volley: ${error.message}"

                Toast.makeText(this@TambahHargaActivity, "Gagal Simpan! Cek Logcat", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    // "Kunci" di sebelah kiri harus sama dengan $request->kunci di Laravel
                    "idProduk" to b.etNamaProduk.text.toString(), // Kirim ID produk (prd_xxx)
                    "kanalHarga" to b.etNamaKanal.text.toString(),
                    "namaHarga" to "Harga Baru", // Sebagai pelengkap saja
                    "hargaSatuan" to b.etHarga.text.toString(),
                    "aktif" to if (b.cbKanalAktif.isChecked) "1" else "0",
                    "hargaUtama" to if (b.cbDefaultKasir.isChecked) "1" else "0"
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Accept" to "application/json")
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}