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

    private val listParameter =
        ArrayList<HashMap<String, String>>()

    private val urlGet =
        "http://192.168.1.22:8000/api/parameter-produk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityParameterBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupRecyclerView()

        b.btnTambahParameter.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TambahParameterActivity::class.java
                )
            )
        }

        b.btnBack.setOnClickListener {
            finish()
        }

        getData()
    }

    private fun setupRecyclerView() {

        adapter =
            ParameterAdapter(this, listParameter)

        b.rvParameter.layoutManager =
            LinearLayoutManager(this)

        b.rvParameter.adapter =
            adapter
    }

    fun getData() {

        val request = StringRequest(
            Request.Method.GET,
            urlGet,

            { response ->

                try {

                    listParameter.clear()

                    val jsonObject =
                        JSONObject(response)

                    val jsonArray =
                        jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {

                        val obj =
                            jsonArray.getJSONObject(i)

                        val map =
                            HashMap<String, String>()

                        map["id"] =
                            obj.optString("id", "")

                        map["namaProduk"] =
                            obj.optString("namaProduk", "")

                        map["hasilPerProduksi"] =
                            obj.optString("hasilPerProduksi", "")

                        map["satuanHasil"] =
                            obj.optString("satuanHasil", "")

                        listParameter.add(map)
                    }

                    adapter.notifyDataSetChanged()

                } catch (e: Exception) {

                    Log.e(
                        "PARAM_PARSE",
                        e.message.toString()
                    )
                }
            },

            { error ->

                Log.e(
                    "PARAM_ERROR",
                    error.toString()
                )

                Toast.makeText(
                    this,
                    error.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this)
            .add(request)
    }

    override fun onResume() {
        super.onResume()

        getData()
    }
}