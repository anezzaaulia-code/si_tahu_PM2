package anezza.aulia.si_tahu_pm2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import anezza.aulia.si_tahu_pm2.databinding.ActivityParameterBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ParameterActivity : AppCompatActivity() {

    private lateinit var b: ActivityParameterBinding
    private lateinit var adapter: ParameterAdapter

    private val listParameter = ArrayList<HashMap<String, String>>()
    private val urlGet = ApiConfig.PARAMETER_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityParameterBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupRecyclerView()

        b.btnTambahParameter.setOnClickListener {
            startActivity(Intent(this, TambahParameterActivity::class.java))
        }

        b.btnBack.setOnClickListener { finish() }

        getData()
    }

    private fun setupRecyclerView() {
        adapter = ParameterAdapter(this, listParameter)
        b.rvParameter.layoutManager = LinearLayoutManager(this)
        b.rvParameter.adapter = adapter
    }

    fun getData() {
        val request = StringRequest(
            Request.Method.GET,
            urlGet,
            { response ->
                try {
                    listParameter.clear()

                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val map = HashMap<String, String>()

                        map["id"] = obj.optString("id", "")
                        map["idProduk"] = obj.optString("idProduk", "")
                        map["kodeProduk"] = obj.optString("kodeProduk", "")
                        map["namaProduk"] = obj.optString("namaProduk", "")
                        map["hasilPerProduksi"] = obj.optString("hasilPerProduksi", "")
                        map["satuanHasil"] = obj.optString("satuanHasil", "")
                        map["aktif"] = obj.boolString("aktif")
                        map["catatan"] = obj.optString("catatan", "")

                        listParameter.add(map)
                    }

                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("PARAM_PARSE", e.message.toString())
                    Toast.makeText(this, "Gagal parsing data parameter", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val pesan = error.networkResponse?.data?.let { String(it) } ?: error.toString()
                Log.e("PARAM_ERROR", pesan)
                Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}

private fun JSONObject.boolString(key: String): String {
    return when (val value = opt(key)) {
        is Boolean -> if (value) "1" else "0"
        is Number -> if (value.toInt() != 0) "1" else "0"
        else -> if (optString(key).equals("true", true) || optString(key) == "1") "1" else "0"
    }
}
