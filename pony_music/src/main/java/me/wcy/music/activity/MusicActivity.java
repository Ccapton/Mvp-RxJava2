package me.wcy.music.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.wcy.music.R;
import me.wcy.music.adapter.FragmentAdapter;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Extras;
import me.wcy.music.executor.NaviMenuExecutor;
import me.wcy.music.executor.WeatherExecutor;
import me.wcy.music.fragment.LocalMusicFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.PlaylistFragment;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.RemoteControlReceiver;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.SystemUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;

public class MusicActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivMenu;
    private ImageView ivSearch;
    private TextView tvLocalMusic;
    private TextView tvOnlineMusic;
    private TextView nameTv;
    private ViewPager mViewPager;
    private FrameLayout flPlayBar;
    private ImageView ivPlayBarCover;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarArtist;
    private ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private ProgressBar mProgressBar;

    private View vNavigationHeader;
    private ImageView imageView;
    private ImageView meIv;
    private LocalMusicFragment mLocalMusicFragment;
    private PlaylistFragment mPlaylistFragment;
    private PlayFragment mPlayFragment;
    private AudioManager mAudioManager;
    private ComponentName mRemoteReceiver;
    private boolean isPlayFragmentShow = false;
    private MenuItem timerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        bindView();
        if (!checkServiceAlive()) {
            return;
        }

        getPlayService().setOnPlayEventListener(this);

        setupView();
      //  updateWeather();
        registerReceiver();
        onChangeImpl(getPlayService().getPlayingMusic());
        parseIntent();


    }
    private void bindView() {
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView= (NavigationView) findViewById(R.id.navigation_view);
        ivMenu= (ImageView) findViewById(R.id.iv_menu);
        ivSearch= (ImageView) findViewById(R.id.iv_search);
        tvLocalMusic= (TextView) findViewById(R.id.tv_local_music);
        tvOnlineMusic= (TextView) findViewById(R.id.tv_online_music);
        mViewPager= (ViewPager) findViewById(R.id.viewpager);
        flPlayBar= (FrameLayout) findViewById(R.id.fl_play_bar);
        ivPlayBarCover= (ImageView) findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle= (TextView) findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist= (TextView) findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay= (ImageView) findViewById(R.id.iv_play_bar_play);
        ivPlayBarNext= (ImageView) findViewById(R.id.iv_play_bar_next);
        mProgressBar= (ProgressBar) findViewById(R.id.pb_play_bar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void setListener() {
        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        imageView.setOnClickListener(this);
    }

    private void setupView() {
        // add navigation header
         vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header2, navigationView, false);
         navigationView.addHeaderView(vNavigationHeader);
        imageView= (ImageView)vNavigationHeader.findViewById(R.id.imageView);
        meIv= (ImageView)vNavigationHeader.findViewById(R.id.meIv);
        nameTv= (TextView) vNavigationHeader.findViewById(R.id.nameTv);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.shashengwan);
        Bitmap gaussianBitmap= GaussianBlurImageProcessor.fastGaussianBlur(bitmap,20,false);
        imageView.setImageBitmap(gaussianBitmap);

        // setup view pager
        mLocalMusicFragment = new LocalMusicFragment();
        mPlaylistFragment = new PlaylistFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mPlaylistFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);
    }
    private AlertDialog dialog(){
        AlertDialog  alertDialog = new AlertDialog.Builder(this).create();
        View dialogView = View.inflate(this, R.layout.about_me_dialog_latyout, null);
        TextView aboutTv = (TextView) dialogView.findViewById(R.id.aboutTv);
        Button sendMailBtn = (Button) dialogView.findViewById(R.id.sendMailBtn);
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(dialogView, 50, 50, 50, 50);
        aboutTv.append(getString(R.string.aboutMe) + "\n");
        aboutTv.append("微信号：Ccapton");
        sendMailBtn.setOnClickListener(this);
        return alertDialog;
    }
/*
    private void updateWeather() {
        PermissionReq.with(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        new WeatherExecutor(getPlayService(), vNavigationHeader).execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(getString(R.string.no_permission, "位置信息", "更新天气"));
                    }
                })
                .request();
    }
*/

    private void registerReceiver() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mRemoteReceiver = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteReceiver);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
        if (mPlayFragment != null) {
            mPlayFragment.onChange(music);
        }
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
        if (mPlayFragment != null) {
            mPlayFragment.onPlayerStart();
        }
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
        if (mPlayFragment != null) {
            mPlayFragment.onPlayerPause();
        }
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
        if (mPlayFragment != null) {
            mPlayFragment.onPublish(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (mPlayFragment != null) {
            mPlayFragment.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(R.id.action_timer);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onMusicListUpdate() {
        if (mLocalMusicFragment != null) {
            mLocalMusicFragment.onMusicListUpdate();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.iv_menu) {
            drawerLayout.openDrawer(GravityCompat.START);
            TranslateAnimation tAnimation= (TranslateAnimation) AnimationUtils.loadAnimation(getApplication(),R.anim.left_in);
            TranslateAnimation tAnimation2= (TranslateAnimation) AnimationUtils.loadAnimation(getApplication(),R.anim.right_in);
            tAnimation.setInterpolator(new BounceInterpolator());
            tAnimation2.setInterpolator(new BounceInterpolator());
            meIv.startAnimation(tAnimation);
            nameTv.startAnimation(tAnimation2);
        }else if(v.getId()==R.id.iv_search){
            startActivity(new Intent(this, SearchMusicActivity.class));
        }else if(v.getId()==R.id.tv_local_music){
            mViewPager.setCurrentItem(0);
        }else if(v.getId()==R.id.tv_online_music){
            mViewPager.setCurrentItem(1);
        }else if(v.getId()==R.id.fl_play_bar){
            showPlayingFragment();
        }else if(v.getId()==R.id.iv_play_bar_play){
            play();
        }else if(v.getId()==R.id.iv_play_bar_next){
            next();
        }else if(v.getId()==R.id.imageView){
            dialog().show();
        }else if(v.getId()==R.id.sendMailBtn){
            sendEmail();
        }
    }
    private void sendEmail() {
        Uri uri = Uri.parse("mailto:chenweibin1125@foxmail.com");
        String[] email = {"chenweibin1125@foxmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "你好，陈尉斌");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, "选择邮件客户端"));
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return NaviMenuExecutor.onNavigationItemSelected(item, this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(getPlayService().isPlaying() || getPlayService().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) getPlayService().getCurrentPosition());

        if (mLocalMusicFragment != null) {
            mLocalMusicFragment.onItemPlay();
        }
    }

    private void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 切换夜间模式不保存状态
    }

    @Override
    protected void onDestroy() {
        if (mRemoteReceiver != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mRemoteReceiver);
        }
        PlayService service = AppCache.getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
        }
        super.onDestroy();
    }
}
