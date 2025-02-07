package iss.nus.edu.sg.mygo.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AccommodationMediaImageService {
    @GET("/proxy/media/{uuid}?fileType=Small%20Thumbnail")
    fun getMediaImage(@Path("uuid") uuid: String): Call<String>
}
