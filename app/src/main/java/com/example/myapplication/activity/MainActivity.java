package com.example.myapplication.activity;

import  androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.LoaiSpAdapter;
import com.example.myapplication.adapter.SanPhamMoiAdapter;
import com.example.myapplication.model.LoaiSp;
import com.example.myapplication.model.LoaiSpModel;
import com.example.myapplication.model.SanPhamMoi;
import com.example.myapplication.model.SanPhamMoiModel;
import com.example.myapplication.model.User;
import com.example.myapplication.retrofit.Apibanhang;
import com.example.myapplication.retrofit.RetrofitClient;
import com.example.myapplication.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Apibanhang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(Apibanhang.class);
        Paper.init(this);
        if(Paper.book().read("user")==null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        Anhxa();
        Actionbar();

        if(isConnected(this )){
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }
        else{
            Toast.makeText(getApplicationContext(), "Không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(trangchu);
                        break;
                    case 1:
                        Intent quanaonam = new Intent(getApplicationContext(), MaleActivity.class);
                        quanaonam.putExtra("loai", 1);
                        startActivity(quanaonam);
                        break;
                    case 2:
                        Intent quanaonu = new Intent(getApplicationContext(), MaleActivity.class);
                        quanaonu.putExtra("loai", 2);
                        startActivity(quanaonu);
                        break;
                    case 3:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 4:
                        // xoa key user
                        Paper.book().delete("user");
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

        private void getSpMoi() {
            compositeDisposable.add(apiBanHang.getSpMoi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sanPhamMoiModel -> {
                                if (sanPhamMoiModel.isSuccess()){
                                    mangSpMoi = sanPhamMoiModel.getResult();
                                    spAdapter=new SanPhamMoiAdapter(getApplicationContext(),mangSpMoi);
                                    recyclerViewManHinhChinh.setAdapter(spAdapter);
                                }
                            },
                            throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    ));
        }


    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            System.out.println(loaiSpModel.isSuccess());
                            if (loaiSpModel.isSuccess()){
                                mangloaisp = loaiSpModel.getResult();
                                mangloaisp.add(new LoaiSp("Đăng xuất", "https://icon-library.com/images/log-on-icon/log-on-icon-23.jpg"));
                                loaiSpAdapter=new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }
    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://htsoftbizman.net/wp-content/uploads/2015/01/quan-ly-khuyen-mai.jpg");
        mangquangcao.add("https://cdnimg.vietnamplus.vn/uploaded/qrndqxjwp/2021_11_23/260121502_178699567733614_6017948394219322686_n_2.jpg");
        mangquangcao.add("https://inhat.vn/wp-content/uploads/2022/01/shop-qu%E1%BA%A7n-%C3%A1o-Quy-Nh%C6%A1n-1-min-1068x801.jpg");
        for(int i=0;i<mangquangcao.size();i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }


    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar =  findViewById(R.id.toolbarmhc);
        viewFlipper= findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh=findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh=findViewById(R.id.listviewmanhinhchinh);
        navigationView=findViewById(R.id.navigationview);
        drawerLayout=findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout =findViewById(R.id.framegiohang);
//        khoi tao List
        mangloaisp= new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Paper.book().read("giohang") != null){
            Utils.manggiohang = Paper.book().read("giohang");
        }

        if(Utils.manggiohang==null){
            Utils.manggiohang=new ArrayList<>();
        }
        else {
            int totalItem = 0;
            for(int i=0; i<Utils.manggiohang.size();i++){
                totalItem = totalItem +Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang =new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

    imgsearch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
    });

    }


    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i=0; i<Utils.manggiohang.size();i++){
            totalItem = totalItem +Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobie = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobie != null && mobie.isConnected()) ){
            return  true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}



