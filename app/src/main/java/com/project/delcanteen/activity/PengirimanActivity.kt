package com.project.delcanteen.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.project.delcanteen.R
import com.project.delcanteen.adapter.AdapterKurir
import com.project.delcanteen.app.ApiConfigAlamat
import com.project.delcanteen.helper.Helper
import com.project.delcanteen.helper.SharedPref
import com.project.delcanteen.model.Chekout
import com.project.delcanteen.model.rajaongkir.Costs
import com.project.delcanteen.model.rajaongkir.ResponOngkir
import com.project.delcanteen.room.MyDatabase
import com.project.delcanteen.util.ApiKey
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.activity_pengiriman.btn_tambahAlamat
import kotlinx.android.synthetic.main.toolbar_custom.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanActivity : AppCompatActivity(){

    lateinit var myDB : MyDatabase
    var totalHarga = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        Helper().setToolbar(this, toolbar, "Pengiriman")
        myDB = MyDatabase.getInstance(this)!!

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        tv_totalBelanja.text = Helper().changeRupiah(totalHarga)
        mainButton()
        setSpiner()
    }

    fun setSpiner(){
        val arryString = ArrayList<String>()
        arryString.add("JNE")
        arryString.add("POS")
        arryString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_spiner, arryString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_kurir.adapter = adapter
        spn_kurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0){
                    getOngkir(spn_kurir.selectedItem.toString())
                }
            }

        }
    }



    @SuppressLint("SetTextI18n")
    fun checkAlamat(){

        if(myDB.daoAlamat().getByStatus(true) != null){
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metodePengiriman.visibility = View.VISIBLE

            val a = myDB.daoAlamat().getByStatus(true)!!
            tv_nama.text = a.name
            tv_phone.text = a.phone
            tv_alamat.text = a.alamat + ", " + a.kota + "," + "," + a.kodepos + ", (" + a.type + ")"
            btn_tambahAlamat.text = "Ubah alamat"

            getOngkir("JNE")
        }else{
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            btn_tambahAlamat.text = "tambah alamat"
        }
    }

    private fun mainButton(){
        btn_tambahAlamat.setOnClickListener{
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            startActivity(Intent(this, PembayaranActivity::class.java))
        }
    }

    private fun bayar() {
        val user = SharedPref(this).getUser()!!
        val a = myDB.daoAlamat().getByStatus(true)!!

        val listProduk = myDB.daoKeranjang().getAll() as ArrayList

        var totalItem = 0
        var totalHarga = 0
        val produks = ArrayList<Chekout.Item>()
        for (p in listProduk) {
            if (p.selected) {
                totalItem += p.jumlah
                totalHarga += (p.jumlah * Integer.valueOf(p.harga))

                val produk = Chekout.Item()
                produk.id = "" + p.id
                produk.total_item = "" + p.jumlah
                produk.total_harga = "" + (p.jumlah * Integer.valueOf(p.harga))
                produk.catatan = "catatan baru"
                produks.add(produk)
            }
        }

        val chekout = Chekout()
        chekout.user_id = "" + user.id
        chekout.total_item = "" + totalItem
        chekout.total_harga = "" + totalHarga
        chekout.name = a.name
        chekout.phone = a.phone
        chekout.jasa_pengiriaman = jasaKirim
        chekout.ongkir = ongkir
        chekout.kurir = kurir
        chekout.detail_lokasi = tv_alamat.text.toString()
        chekout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
        chekout.produks = produks

        val json = Gson().toJson(chekout, Chekout::class.java)
        Log.d("Respon:", "jseon:" + json)
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)

   //ApiConfig.instanceRetrofit.Checkout(chekout).enqueue(object : Callback<ResponModel> {
        //override fun onFailure(call: Call<ResponModel>, t: Throwable) {
        //Toast.makeText(this@PengirimanActivity, "Error:"+t.message, Toast.LENGTH_SHORT).show()
         //}

         //override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
        //val respon = response.body()!!
        //if (respon.success == 1){
        //Toast.makeText(this@PengirimanActivity, "Berhasil Kirim ke server ", Toast.LENGTH_SHORT).show()
        //} else{
        //Toast.makeText(this@PengirimanActivity, "Error:"+respon.message, Toast.LENGTH_SHORT).show()
        //}
       //}
        //})
}

    private fun getOngkir(kurir:String){

        val alamat = myDB.daoAlamat().getByStatus(true)

        val origin = "501"
        val destination = "" + alamat!!.id_kota.toString()
        val berat = 1000

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.toLowerCase()).enqueue(object : Callback<ResponOngkir> {
            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {
                if (response.isSuccessful) {

                    Log.d("Success", "berhasil memuat data")
                    val result = response.body()!!.rajaongkir.result
                    if (result.isNotEmpty()) {
                        displayOngkir(result[0].code.toUpperCase(), result[0].costs)
                    }


                } else {
                    Log.d("Error", "gagal memuat data:" + response.message())
                }
            }

            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error", "gagal memuat data:" + t.message)
            }

        })
    }
    var ongkir = ""
    var kurir = ""
    var jasaKirim = ""
    private fun displayOngkir(_kurir:String, arrayList : ArrayList<Costs>){

        var arrayOngkir = ArrayList<Costs>()
        for(i in arrayList.indices){
            val ongkir = arrayList[i]
            if (i == 0){
                ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }

        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        jasaKirim = arrayOngkir[0].service

        val layoutManager1 = LinearLayoutManager(this)
        layoutManager1.orientation = LinearLayoutManager.VERTICAL
        var adapter: AdapterKurir? = null
        adapter = AdapterKurir(arrayOngkir, kurir, object : AdapterKurir.Listeners{
            override fun onClicked(data: Costs, index: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir){
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }
        })
        rv_metode.adapter = adapter
        rv_metode.layoutManager = layoutManager1
    }

    fun setTotal(ongkir :String) {
        tv_ongkir.text = Helper().changeRupiah(ongkir)
        tv_total.text = Helper().changeRupiah(Integer.valueOf(ongkir) + totalHarga)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        checkAlamat()
        super.onResume()
    }
}