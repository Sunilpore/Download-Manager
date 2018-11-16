package com.example.sunil.downloadmanager10.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ServiceCall {
    @GET
    Observable <ResponseBody> getFileDownloadData(@Url String url);
}
