package anezza.aulia.si_tahu_pm2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anezza.aulia.si_tahu_pm2.databinding.ActivityTambahParameterBinding

class TambahParameterActivity : AppCompatActivity() {
    private lateinit var b: ActivityTambahParameterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahParameterBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Tombol Back sekarang sudah ada ID-nya di XML (btnBack)
        b.btnBack.setOnClickListener {
            finish()
        }

        b.btnSimpanParameter.setOnClickListener {
            if (b.etHasilMasak.text.toString().isNotEmpty()) {
                Toast.makeText(this, "Data Siap Dikirim!", Toast.LENGTH_SHORT).show()
            } else {
                b.etHasilMasak.error = "Wajib diisi"
            }
        }
    }
}