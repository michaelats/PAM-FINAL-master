package com.project.delcanteen.app

import com.project.delcanteen.model.Chekout
import com.project.delcanteen.model.ResponModel
import com.project.delcanteen.model.rajaongkir.ResponOngkir
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name :String,
        @Field("email") email :String,
        @Field("phone") phone :String,
        @Field("noktp") noktp :String,
        @Field("password") password :String
    ):Call<ResponModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email :String,
        @Field("password") password :String
    ):Call<ResponModel>

    @POST("Checkout")
    fun Checkout(
        @Body data : Chekout
    ):Call<ResponModel>

    @GET("produk")
    fun getProduk(): Call<ResponModel>

    @GET("province")
    fun getProvinsi(
        @Header("key") key :String,
    ): Call<ResponModel>

    @GET("city")
    fun getKota(
        @Query("province") id: String,
        @Header("key") key :String
    ): Call<ResponModel>

    @GET("kecamatan")
    fun getKecamatan(
        @Query("id_kota") id: Int
    ): Call<ResponModel>

    @POST("cost")
    fun ongkir(
        @Header("key") key :String,
        @Field("origin") origin :String,
        @Field("destination") destination :String,
        @Field("weight") weight :Int,
        @Field("courier") courier :String
    ):Call<ResponOngkir>
    @GET("chekout/user/{id}")
    fun getRiwayat(
        @Path("id") id: Int
    ):Call<ResponModel>

    @POST("chekout/batal/{id}")
    fun batalChekout(
        @Path("id") id: Int
    ): Call<ResponModel>

}