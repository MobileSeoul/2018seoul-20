package buskinggo.seoul.com.buskinggo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import buskinggo.seoul.com.buskinggo.dto.UserDTO;

public class MainActivity extends AppCompatActivity {

    HomeFragment homeFragment;
    BuskingListFragment buskingListFragment;
    BuskingRegisterFragment buskingRegisterFragment;
    MyPageFragment myPageFragment;
    UserDTO userDTO;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = getIntent();
        userDTO = (UserDTO) mainIntent.getSerializableExtra("userDTO");

        homeFragment = new HomeFragment();
        buskingListFragment = new BuskingListFragment();
        buskingRegisterFragment = new BuskingRegisterFragment();
        myPageFragment = new MyPageFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home: // 홈
                        findViewById(R.id.action_home).setClickable(false);
                        findViewById(R.id.busking_list).setClickable(true);
                        findViewById(R.id.busking_regist).setClickable(true);
                        findViewById(R.id.my_page).setClickable(true);
                        changeFragment(homeFragment);
                        break;
                    case R.id.busking_list: // 버스킹 목록
                        findViewById(R.id.action_home).setClickable(true);
                        findViewById(R.id.busking_list).setClickable(false);
                        findViewById(R.id.busking_regist).setClickable(true);
                        findViewById(R.id.my_page).setClickable(true);
                        changeFragment(buskingListFragment);
                        break;
                    case R.id.busking_regist: // 버스킹 등록
                        findViewById(R.id.action_home).setClickable(true);
                        findViewById(R.id.busking_list).setClickable(true);
                        findViewById(R.id.busking_regist).setClickable(false);
                        findViewById(R.id.my_page).setClickable(true);
                        changeFragment(buskingRegisterFragment);
                        break;
                    case R.id.my_page: // 마이페이지
                        findViewById(R.id.action_home).setClickable(true);
                        findViewById(R.id.busking_list).setClickable(true);
                        findViewById(R.id.busking_regist).setClickable(true);
                        findViewById(R.id.my_page).setClickable(false);
                        changeFragment(myPageFragment);
                        break;
                }
                return true;
            }
        });

    }

    /*
    *  네비게이션 바 클릭시 fragment 교체
    * */
    public void changeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    // 두번 뒤로가기 누르면 종료되도록
    private long lastTimeBackPressed;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 눌러 종료합니다", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
