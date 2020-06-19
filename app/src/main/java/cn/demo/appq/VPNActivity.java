package cn.demo.appq;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blankj.utilcode.util.NetworkUtils;
import com.github.megatronking.netbare.NetBareListener;

public class VPNActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_p_n);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NetBarePresenter netBarePresenter = new NetBarePresenter(this, new NetBareListener() {
            @Override
            public void onServiceStarted() {
                Snackbar.make(toolbar, "Vpn Service Started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Snackbar.make(toolbar, "See http://" + NetworkUtils.getIpAddressByWifi() + ":8080", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }

            @Override
            public void onServiceStopped() {
                Snackbar.make(toolbar, "Vpn Service Stopped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netBarePresenter.start();
                netBarePresenter.prepareNetBare();
                netBarePresenter.startVpn();
            }
        });
    }

}
