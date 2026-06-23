package com.pydroid.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.pydroidide.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private View splashLayout;
    private static final int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashLayout = findViewById(R.id.splash_layout);
        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);

        if (viewPager == null || splashLayout == null || bottomNav == null) {
            finish();
            return;
        }

        checkPermissions();
        setupViewPager();

        new Handler().postDelayed(() -> {
            splashLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            bottomNav.setVisibility(View.VISIBLE);
        }, 1800);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new EditorFragment(), "编辑器");
        adapter.addFragment(new TerminalFragment(), "终端");
        adapter.addFragment(new AIAssistantFragment(), "AI助手");
        adapter.addFragment(new FileManagerFragment(), "文件");
        viewPager.setAdapter(adapter);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_editor) viewPager.setCurrentItem(0);
            else if (id == R.id.nav_terminal) viewPager.setCurrentItem(1);
            else if (id == R.id.nav_ai) viewPager.setCurrentItem(2);
            else if (id == R.id.nav_files) viewPager.setCurrentItem(3);
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                Toast.makeText(this, "需要存储权限才能正常使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
