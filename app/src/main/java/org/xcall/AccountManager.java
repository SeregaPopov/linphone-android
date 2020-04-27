package org.xcall;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.core.AuthInfo;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;

public class AccountManager {

    private final Context mContext;
    // private String mCurrency = "Euro";
    // private double mMoney = 14.76;
    RequestQueue queue;
    TextView moneyTextView;

    public AccountManager(Context context) {
        mContext = context;
    }

    public String MoneyString() {
        // String tmp = mCurrency + ": " + String.valueOf(mMoney);
        return "tmp";
    }

    public void UpdateMoneyRequest() {

        Core core = LinphoneManager.getCore();
        ProxyConfig proxyConf = core.getDefaultProxyConfig();
        AuthInfo auth = proxyConf.findAuthInfo();
        String ha1 = auth.getHa1();
        // String userId = auth.getUserid();
        String userName = auth.getUsername();

        // final String url = "http://5.9.118.112/usr/balance.php?username=" + userName + "&hash=" +
        // ha1;
        final String url =
                "https://xcall.tmb-ix.net/usr/balance.php?username=" + userName + "&hash=" + ha1;

        moneyTextView = (TextView) ((Activity) mContext).findViewById(R.id.money_text);

        queue = Volley.newRequestQueue(mContext);
        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (moneyTextView != null)
                                    moneyTextView.setText(response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Log.d("error",error.toString());
                            }
                        });
        queue.add(request);
    }

    // Запрос завершен. Обновляем информацию
    public void onMoneyRequestFinish() {}

    // Отправка запроса по таймеру

    // Отправка запроса после завершения звонка

}
