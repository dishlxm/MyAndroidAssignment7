package zju.edu.mymediaplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GlideActivity extends AppCompatActivity {

    ViewPager vpager = null;
    LayoutInflater layoutInflater = null;
    List<View> pages = new ArrayList<View>();
    Button imageButton;
    String path;
    String filepath;
    ViewAdapter adapter = new ViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        layoutInflater = getLayoutInflater();
        imageButton = (Button)findViewById(R.id.image_button);
        vpager = (ViewPager) findViewById(R.id.view_pager);
        addImage(R.drawable.drawableimage);
        addImage(R.drawable.ic_markunread);
        addImage("file:///android_asset/assetsimage.jpg");
        addImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1562328963756&di=9c0c6c839381c8314a3ce8e7db61deb2&imgtype=0&src=http%3A%2F%2Fpic13.nipic.com%2F20110316%2F5961966_124313527122_2.jpg");
        if (!checkPermissionAllGranted(mPermissionsArrays)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mPermissionsArrays, REQUEST_PERMISSION);
            }
        } else{
            addSDPic();
        }
        adapter.setDatas(pages);
        vpager.setAdapter(adapter);

    }

    public void addSDPic(){
        File sd = Environment.getExternalStorageDirectory();
        path = sd.getPath();
        filepath = path + "/birthday.jpg";
        addImage(filepath);
        adapter.setDatas(pages);
        vpager.setAdapter(adapter);

    }

    private void addImage(int resId) {
        ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.image_view, null);
        Glide.with(this)
                .load(resId)
                .error(R.drawable.error)
                .into(imageView);
        pages.add(imageView);
    }

    private void addImage(String path) {
        final ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.image_view, null);
        Glide.with(this)
                .load(path)
                .error(R.drawable.error)
                .into(imageView);
        pages.add(imageView);
    }

    private String[] mPermissionsArrays = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private final static int REQUEST_PERMISSION = 123;

    private boolean checkPermissionAllGranted(String[] permissions) {
        // 6.0以下不需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissionAllGranted(mPermissionsArrays))
            addSDPic();
    }
}
