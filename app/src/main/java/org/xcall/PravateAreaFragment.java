package org.xcall;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Locale;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.core.AuthInfo;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;

public class PravateAreaFragment extends Fragment implements View.OnClickListener {

    // public static final String DOMAIN = "http://5.9.118.112";
    public static final String DOMAIN = "https://xcall.tmb-ix.net";

    private boolean _firstTime = false;
    private TextView mInfo, mReport, mRates, mPay;
    private View mInfoSelected, mReportSelected, mRatesSelected, mPaySelected;

    private WebView xcallWebView;

    private String mHA1 = "";
    private String mUserName = "";

    public void IsFirstTime(boolean isFirstTime) {
        _firstTime = isFirstTime;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.private_area_fragment, container, false);

        mInfo = rootView.findViewById(R.id.lk_info);
        mInfo.setOnClickListener(this);
        mInfoSelected = rootView.findViewById(R.id.lk_info_select);

        mReport = rootView.findViewById(R.id.lk_report);
        mReport.setOnClickListener(this);
        mReportSelected = rootView.findViewById(R.id.lk_report_select);

        mRates = rootView.findViewById(R.id.lk_rates);
        mRates.setOnClickListener(this);
        mRatesSelected = rootView.findViewById(R.id.lk_rates_select);

        mPay = rootView.findViewById(R.id.lk_pay);
        mPay.setOnClickListener(this);
        mPaySelected = rootView.findViewById(R.id.lk_pay_select);

        xcallWebView = (WebView) rootView.findViewById(R.id.webview);
        WebSettings webSettings = xcallWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        xcallWebView.getSettings().setAppCacheEnabled(false);
        xcallWebView.setWebViewClient(
                new XCallWebViewClient() {
                    @Override
                    public void onReceivedError(
                            WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        xcallWebView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        xcallWebView.setVisibility(View.VISIBLE);
                    }
                });

        if (_firstTime) {
            updateView(R.id.lk_rates);
        } else updateView(R.id.lk_info);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        updateView(id);
    }

    private void updateView(int id) {
        String path = "/usr/info.php?";

        if (id == R.id.lk_info) {
            mInfo.setEnabled(false);
            mInfoSelected.setVisibility(View.VISIBLE);

            mReportSelected.setVisibility(View.INVISIBLE);
            mRatesSelected.setVisibility(View.INVISIBLE);
            mPaySelected.setVisibility(View.INVISIBLE);

            mReport.setEnabled(true);
            mRates.setEnabled(true);
            mPay.setEnabled(true);

            path = "/usr/info.php?";
            // xcallWebView.loadUrl(DOMAIN + "/usr/info.php?" + authInfo() + langInfo());
            // refresh();
        }
        if (id == R.id.lk_report) {
            mReport.setEnabled(false);
            mReportSelected.setVisibility(View.VISIBLE);

            mInfoSelected.setVisibility(View.INVISIBLE);
            mRatesSelected.setVisibility(View.INVISIBLE);
            mPaySelected.setVisibility(View.INVISIBLE);

            mInfo.setEnabled(true);
            mRates.setEnabled(true);
            mPay.setEnabled(true);

            path = "/usr/report.php?";
            // xcallWebView.loadUrl(DOMAIN + "/usr/report.php?" + authInfo() + langInfo());
        }
        if (id == R.id.lk_rates) {
            mRates.setEnabled(false);
            mRatesSelected.setVisibility(View.VISIBLE);

            mInfoSelected.setVisibility(View.INVISIBLE);
            mReportSelected.setVisibility(View.INVISIBLE);
            mPaySelected.setVisibility(View.INVISIBLE);

            mInfo.setEnabled(true);
            mReport.setEnabled(true);
            mPay.setEnabled(true);

            path = "/usr/rates.php?";
            // xcallWebView.loadUrl(DOMAIN + "/usr/rates.php?" + authInfo() + langInfo());
        }
        if (id == R.id.lk_pay) {
            mPay.setEnabled(false);
            mPaySelected.setVisibility(View.VISIBLE);

            mInfoSelected.setVisibility(View.INVISIBLE);
            mReportSelected.setVisibility(View.INVISIBLE);
            mRatesSelected.setVisibility(View.INVISIBLE);

            mInfo.setEnabled(true);
            mReport.setEnabled(true);
            mRates.setEnabled(true);

            path = "/usr/pay.php?";
            // xcallWebView.loadUrl(DOMAIN + "/usr/pay.php?" + authInfo() + langInfo());
        }
        String req = DOMAIN + path + authInfo() + langInfo();
        xcallWebView.loadUrl(DOMAIN + path + authInfo() + langInfo());
    }

    private String authInfo() {
        if (mUserName.isEmpty() || mHA1.isEmpty()) {
            Core core = LinphoneManager.getCore();
            ProxyConfig proxyConf = core.getDefaultProxyConfig();
            AuthInfo auth = proxyConf.findAuthInfo();
            mHA1 = auth.getHa1();
            mUserName = auth.getUsername();
        }
        return "username=" + mUserName + "&hash=" + mHA1;
    }

    private String langInfo() {
        return "&lang=" + Locale.getDefault().getLanguage();
    }
}
