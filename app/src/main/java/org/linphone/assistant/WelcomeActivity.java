package org.linphone.assistant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Locale;
import org.linphone.R;
import org.linphone.settings.LinphonePreferences;

public class WelcomeActivity extends AssistantActivity {

    // Popov: Аналитика по валидациям смс
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Spanned policy = Html.fromHtml(getString(R.string.agree_terms_privacy));
        TextView termsOfUse = (TextView) findViewById(R.id.termsOfUse);
        termsOfUse.setText(policy);
        termsOfUse.setMovementMethod(LinkMovementMethod.getInstance());

        termsOfUse.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("welcome_terms_or_privacy_click", null);
                    }
                });

        TextView accountCreation = findViewById(R.id.btnWelcomeRegister);
        accountCreation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("welcome_next_click", null);
                        Intent intent =
                                new Intent(
                                        WelcomeActivity.this,
                                        PhoneAccountCreationAssistantActivity.class);
                        startActivity(intent);
                    }
                });

        TextView rates = findViewById(R.id.btnWelcomeRates);
        rates.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("welcome_rates_click", null);

                        String ratesPage =
                                "https://xcall.tmb-ix.net/usr/rates.php?lang="
                                        + Locale.getDefault().getLanguage();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ratesPage));
                        startActivity(browserIntent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getResources()
                .getBoolean(R.bool.forbid_to_leave_assistant_before_account_configuration)) {
            mBack.setEnabled(false);
        }

        mBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinphonePreferences.instance().firstLaunchSuccessful();
                        goToLinphoneActivity();
                    }
                });
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources()
                    .getBoolean(R.bool.forbid_to_leave_assistant_before_account_configuration)) {
                // Do nothing
                return true;
            } else {
                LinphonePreferences.instance().firstLaunchSuccessful();
                goToLinphoneActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
     */
}
