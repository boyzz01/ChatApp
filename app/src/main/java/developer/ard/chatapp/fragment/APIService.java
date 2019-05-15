package developer.ard.chatapp.fragment;

import developer.ard.chatapp.notifikasi.MyResponse;
import developer.ard.chatapp.notifikasi.Sender;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAJiBGcRY:APA91bFRhZJKFC1mH3M6ef_hoa3m-BYVksg0knjJ5OXelGwJG_-fTz7bKojEECm45keChVQ5fQ6hGN3UymwN5PjAXSCplhiZUCbSD6iDlvhzH9hWBpRVD3Vn3ZIHN1sOKFdbZmkrp9_Q"
                    
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
