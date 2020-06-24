package com.example.oishi.purchase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oishi.FoodDetailActivity;
import com.example.oishi.R;
import com.example.oishi.RequestHttpURLConnection;
import com.example.oishi.review.ReviewItem;
import com.example.oishi.review.ReviewItemAdapter;
import com.example.oishi.review.ReviewLayoutManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class americano extends AppCompatActivity {
    int count = 1;
    public String store_title, FoodName, StoreName, FoodPrice, FoodImg, local, Food_Type;

    public ArrayList<ReviewItem> visibleItems = new ArrayList<>();
    Context mContext = americano.this;
    PurchaseRecyclerView recyclerView;

    private static final String TAG_UID = "id";
    private static final String TAG_CONTENT ="content";
    private static final String TAG_RATING = "rating";
    private static final String TAG_TIME = "present";
    ArrayList<HashMap<String, String>> review_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_americano);

        Button COUNT_DOWN = findViewById(R.id.count_down);
        Button COUNT_UP = findViewById(R.id.count_up);
        final TextView COUNT_NUMBER = findViewById(R.id.count_number);
        final TextView BASE_PRICE = findViewById(R.id.menu_base_price);
        final TextView TITLE = findViewById(R.id.title);
        final TextView Store_Title = findViewById(R.id.store_name_Text);
        final ImageView Food_Img = findViewById(R.id.menu_image);
        final Button Shopping_Basket = findViewById(R.id.put_shopping_basket);

        //***********************************************************************************************
        //FoodDetailActivity에서 가게이름, 음식이름, 음식가격, 음식 이미지, 음식유형을 받아서 그 값을 띄워줍니다.
        Intent intent = getIntent();
        FoodName = intent.getExtras().getString("foodname");
        StoreName = intent.getExtras().getString("storename");
        FoodPrice = intent.getExtras().getString("foodprice");
        FoodImg = intent.getExtras().getString("foodimg");
        Food_Type = intent.getExtras().getString("foodtype");
        TITLE.setText(FoodName);
        System.out.println(FoodName);
        Store_Title.setText(StoreName);
        BASE_PRICE.setText(FoodPrice);
        Food_Img.setImageResource(getResources().getIdentifier(FoodImg, "drawable", this.getPackageName()));

        //***********************************************************************************************
        //php에서 사용할 핸드폰의 고유번호인 Serial number의 값을 가져옵니다.
        local = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        COUNT_NUMBER.setText("" +count+"");
        Context context = this;

        COUNT_UP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                COUNT_NUMBER.setText(String.format("%s", count));
            }
        });

        COUNT_DOWN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if(count < 1)
                {count = 1;}

                COUNT_NUMBER.setText(String.format("%s", count));
            }
        });

        //***********************************************************************************************
        //장바구니 버튼을 클릭하면 http://ykh3587.dothome.co.kr/shopping_basket.php로 연결해서 값들을 데이터베이스에 저장합니다.
        Shopping_Basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url2 = "http://ykh3587.dothome.co.kr/shopping_basket.php";
                ContentValues values = new ContentValues();
                values.put("storename", StoreName);
                values.put("foodname", FoodName);
                values.put("foodprice", FoodPrice);
                values.put("count", count);
                values.put("local", local);
                BackgroundTask task2 = new BackgroundTask(url2, values);
                task2.execute();

                Intent intent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                intent.putExtra("brand_name", StoreName);
                intent.putExtra("foodtype", Food_Type);
                startActivity(intent);

            }
        });

        review_List = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);

        //***********************************************************************************************
        //http://ykh3587.dothome.co.kr/read_review.php로 연결해서 데이터베이스에 저장된 리뷰들을 가져옵니다.
        String url = "http://ykh3587.dothome.co.kr/read_review.php";

        BackgroundTask task = new BackgroundTask(url, null);
        task.execute();

    }
    public class BackgroundTask extends AsyncTask<Void, Void, String> {

        String url;
        ContentValues values;

        BackgroundTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onProgressUpdate(Void ... voids) {
            super.onProgressUpdate(voids);
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            //***********************************************************************************************
            //http://ykh3587.dothome.co.kr/read_review.php로 연결해서 데이터베이스에서 가져온 리뷰들을 adapter와 item들을
            // 이용해서 값들이 저장되고 recyclerview를 이용해서 그 값들을 보여지도록 합니다.
            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(results);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    String uid = c.getString(TAG_UID);
                    String content = c.getString(TAG_CONTENT);
                    String rating = c.getString(TAG_RATING);
                    String time = c.getString(TAG_TIME);

                    HashMap<String,String> persons = new HashMap<>();

                    persons.put(TAG_UID,uid);
                    persons.put(TAG_CONTENT,content);
                    persons.put(TAG_RATING, rating);
                    persons.put(TAG_TIME, time);

                    review_List.add(persons);
/*


                    adapter.addItem(new MenuSortItem(uid, name,content));

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new MenuSortItemLayoutManager(mContext));*/
                }

                ReviewItemAdapter adapter = new ReviewItemAdapter(mContext);

                // 어댑터 생성, R.layout.list_item : Layout ID
                for( int i = 0; i < review_List.size(); i++ ){

                    HashMap<String,String> hashMap = review_List.get(i);
                    adapter.addItem(new ReviewItem(hashMap.get(TAG_UID), hashMap.get(TAG_CONTENT), hashMap.get(TAG_RATING), hashMap.get(TAG_TIME)));
                   //System.out.println(hashMap.get(TAG_UID), hashMap.get(TAG_CONTENT), hashMap.get(TAG_RATING), hashMap.get(TAG_TIME));
                }

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new ReviewLayoutManager(mContext));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                store_title = data.getStringExtra("store_title");
            }
        }
    }
}
