package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahParameterBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TambahParameterActivity : AppCompatActivity() {

    private lateinit var b:
            ActivityTambahParameterBinding

    private val baseUrl =
        "http://192.168.1.22:8000/api/parameter-produk"

    private var mode = "TAMBAH"
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityTambahParameterBinding.inflate(
            layoutInflater
        )

        setContentView(b.root)

        mode =
            intent.getStringExtra("MODE")
                ?: "TAMBAH"

        // MODE EDIT
        if (mode == "EDIT") {

            id =
                intent.getStringExtra("id")
                    ?: ""

            b.etNamaProduk.setText(
                intent.getStringExtra("namaProduk")
            )

            b.etHasilMasak.setText(
                intent.getStringExtra("hasilPerProduksi")
            )

            b.etSatuanHasil.setText(
                intent.getStringExtra("satuanHasil")
            )

            b.btnSimpanParameter.text =
                "Update Parameter"
        }

        b.btnBack.setOnClickListener {
            finish()
        }

        b.btnSimpanParameter.setOnClickListener {

            if (validasi()) {

                if (mode == "EDIT") {
                    updateData()
                } else {
                    simpanData()
                }
            }
        }
    }

    private fun validasi(): Boolean {

        if (b.etNamaProduk.text.toString().isEmpty()) {

            b.etNamaProduk.error =
                "Nama produk wajib diisi"

            return false
        }

        if (b.etHasilMasak.text.toString().isEmpty()) {

            b.etHasilMasak.error =
                "Hasil produksi wajib diisi"

            return false
        }

        if (b.etSatuanHasil.text.toString().isEmpty()) {

            b.etSatuanHasil.error =
                "Satuan wajib diisi"

            return false
        }

        return true
    }

    // INSERT
    private fun simpanData() {

        val request = object : StringRequest(
            Request.Method.POST,
            baseUrl,

            {
                Toast.makeText(
                    this,
                    "Data berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            { error ->

                Toast.makeText(
                    this,
                    error.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams():
                    MutableMap<String, String> {

                return hashMapOf(

                    "namaProduk" to
                            b.etNamaProduk.text.toString(),

                    "hasilPerProduksi" to
                            b.etHasilMasak.text.toString(),

                    "satuanHasil" to
                            b.etSatuanHasil.text.toString()
                )
            }
        }

        Volley.newRequestQueue(this)
            .add(request)
    }

    // UPDATE
    private fun updateData() {

        val urlUpdate =
            "$baseUrl/update/$id"

        val request = object : StringRequest(
            Request.Method.POST,
            urlUpdate,

            {
                Toast.makeText(
                    this,
                    "Data berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            { error ->

                Toast.makeText(
                    this,
                    error.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams():
                    MutableMap<String, String> {

                return hashMapOf(

                    "namaProduk" to
                            b.etNamaProduk.text.toString(),

                    "hasilPerProduksi" to
                            b.etHasilMasak.text.toString(),

                    "satuanHasil" to
                            b.etSatuanHasil.text.toString()
                )
            }
        }

        Volley.newRequestQueue(this)
            .add(request)
    }
}