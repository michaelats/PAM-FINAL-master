package com.project.delcanteen.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.project.delcanteen.MainActivity
import com.project.delcanteen.R
import com.project.delcanteen.adapter.AdapterProduct
import com.project.delcanteen.adapter.AdapterSlider
import com.project.delcanteen.app.ApiConfig
import com.project.delcanteen.model.Produk
import com.project.delcanteen.model.ResponModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    lateinit var vpSlider: ViewPager
    lateinit var rvProduct:RecyclerView
    lateinit var rvProductPopular:RecyclerView
    lateinit var rvProductOther:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        getProduk()


        return view
    }
    fun displayProduk(){
        var arrSlider = ArrayList<Int>()
        arrSlider.add(R.drawable.odol)
        arrSlider.add(R.drawable.slider5)
        arrSlider.add(R.drawable.slider6)

        val adapterSlider = AdapterSlider(arrSlider,activity)
        vpSlider.adapter = adapterSlider

        val layoutManager1 = LinearLayoutManager(activity)
        layoutManager1.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager3 = LinearLayoutManager(activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL



        rvProduct.adapter = AdapterProduct(requireActivity(), listProduk)
        rvProduct.layoutManager = layoutManager1

        rvProductPopular.adapter = AdapterProduct(requireActivity(), listProduk)
        rvProductPopular.layoutManager = layoutManager2

        rvProductOther.adapter = AdapterProduct(requireActivity(), listProduk)
        rvProductOther.layoutManager = layoutManager3
    }
    private var listProduk:ArrayList<Produk> = ArrayList()
    fun getProduk(){
        ApiConfig.instanceRetrofit.getProduk().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if(res.success == 1){
                    var arrayProduk = ArrayList<Produk>()
                    for (p in res.produks){
                        p.discount = 1500
                        arrayProduk.add(p)
                    }
                    listProduk = arrayProduk
                    displayProduk()
                }
            }
        })
    }

    fun init(view: View){
        vpSlider = view.findViewById(R.id.vp_slider)
        rvProduct = view.findViewById(R.id.rv_product)
        rvProductPopular = view.findViewById(R.id.rv_product_popular)
        rvProductOther = view.findViewById(R.id.rv_product_other)
    }
//   val arrProduct:ArrayList<P>get() {
//        val arr = ArrayList<product>()
//        val p1 = product()
//        p1.nama = "Shampo Head and Shoulders 300ml"
//        p1.harga = "Rp50,000"
//        p1.gambar = R.drawable.sampo
//
//        val p2 = product()
//        p2.nama = "Odol Pepsodent"
//        p2.harga = "Rp20,000"
//        p2.gambar = R.drawable.odol
//
//        val p3 = product()
//        p3.nama = "Teh Botol"
//        p3.harga = "Rp5,000"
//        p3.gambar = R.drawable.tehbotol
//
//        val p4 = product()
//        p4.nama = "Nutri Sari"
//        p4.harga = "Rp5,000"
//        p4.gambar = R.drawable.nutrisari
//
//        arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)
//        arr.add(p4)
//
//        return arr
//    }
//    val arrProductPopular:ArrayList<product>get() {
//        val arr = ArrayList<product>()
//        val p1 = product()
//        p1.nama = "Capuchino Cincau"
//        p1.harga = "Rp10,000"
//        p1.gambar = R.drawable.capcin
//
//        val p2 = product()
//        p2.nama = "Nasi Goreng"
//        p2.harga = "Rp20,000"
//        p2.gambar = R.drawable.nasigoreng
//
//        val p3 = product()
//        p3.nama = "Es Teh"
//        p3.harga = "Rp5,000"
//        p3.gambar = R.drawable.esteh
//
//        val p4 = product()
//        p4.nama = "Wagyu A5 Medium Rare"
//        p4.harga = "Rp5,000,000"
//        p4.gambar = R.drawable.wagyu
//
//        arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)
//        arr.add(p4)
//
//        return arr
//    }
//    val arrProductOther:ArrayList<product>get() {
//        val arr = ArrayList<product>()
//        val p1 = product()
//        p1.nama = "Leci Squash"
//        p1.harga = "Rp15,000"
//        p1.gambar = R.drawable.leci
//
//        val p2 = product()
//        p2.nama = "Jus Alpukat"
//        p2.harga = "Rp20,000"
//        p2.gambar = R.drawable.alpukat
//
//        val p3 = product()
//        p3.nama = "Tempe Goreng"
//        p3.harga = "Rp2,000"
//        p3.gambar = R.drawable.tempe
//
//        val p4 = product()
//        p4.nama = "Tahu Isi"
//        p4.harga = "Rp2,000"
//        p4.gambar = R.drawable.tahuisi
//
//        arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)
//        arr.add(p4)
//
//        return arr
//    }

}