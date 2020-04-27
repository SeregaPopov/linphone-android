package org.xcall;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.activities.MainActivity;
import org.linphone.core.Core;

public class PrivateAreaActivity extends MainActivity {
    public static final String NAME = "PrivateArea";

    private boolean mInterfaceLoaded;
    WebView xcallWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra("Activity", NAME);
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_private_area);

        /*
                if (savedInstanceState == null) {
                    // During initial setup, plug in the details fragment.
                    PravateAreaFragment privateArea = new PravateAreaFragment();
                    details.setArguments(getIntent().getExtras());
                    getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
                }
        */

        mInterfaceLoaded = false;

        /*
                LinearLayout fragmentContainer =
                        findViewById(R.id.fragmentContainer);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                fragmentContainer.addView(view, params);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                PravateAreaFragment fragment = new PravateAreaFragment();
                fragmentTransaction.add(R.id.fragmentContainer, fragment);
                fragmentTransaction.commit();
        */
        /*
                // Uses the fragment container layout to inflate the dialer view instead of using a fragment
                new AsyncLayoutInflater(this)
                        .inflate(
                                R.layout.activity_private_area,
                                null,
                                new AsyncLayoutInflater.OnInflateFinishedListener() {
                                    @Override
                                    public void onInflateFinished(
                                            @NonNull View view, int resid, @Nullable ViewGroup parent) {
                                        LinearLayout fragmentContainer =
                                                findViewById(R.id.fragmentContainer);
                                        LinearLayout.LayoutParams params =
                                                new LinearLayout.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT);
                                        fragmentContainer.addView(view, params);
                                        initUI(view);
                                        mInterfaceLoaded = true;
                                    }
                                });
        */
        // handleIntentParams(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment == null) {
            PravateAreaFragment fragment = new PravateAreaFragment();
            changeFragment(fragment, "PrivateArea", false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Clean fragments stack upon return
        while (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPrivateAreaSelected.setVisibility(View.VISIBLE);

        xcallWebView = (WebView) findViewById(R.id.webview);

        xcallWebView.reload();

        Core core = LinphoneManager.getCore();
        if (core != null) {
            // core.addListener(mListener);
        }
    }

    @Override
    protected void onPause() {

        Core core = LinphoneManager.getCore();
        if (core != null) {
            // core.removeListener(mListener);
        }

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void goBack() {
        // 1 is for the empty fragment on tablets
        if (!isTablet() || getFragmentManager().getBackStackEntryCount() > 1) {
            if (popBackStack()) {
                return;
            }
        }
        super.goBack();
    }
}
