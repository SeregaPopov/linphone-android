/*
 * Copyright (c) 2010-2019 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.assistant;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.linphone.R;
import org.linphone.core.AccountCreator;
import org.linphone.core.AccountCreatorListenerStub;
import org.linphone.core.tools.Log;
import org.linphone.settings.LinphonePreferences;

public class PhoneAccountValidationAssistantActivity extends AssistantActivity {
    // private TextView mFinishCreation;
    private EditText mSmsCode;
    private ClipboardManager mClipboard;
    private TextView mPhoneNumber;
    private View waitScreen;

    private int mActivationCodeLength;
    private boolean mIsLinking = false, mIsLogin = false;
    private AccountCreatorListenerStub mListener;

    // Popov: Аналитика по валидациям смс
    private FirebaseAnalytics mFirebaseAnalytics;

    private void smsCodeEntered() {

        mSmsCode.clearFocus();
        mSmsCode.setEnabled(false);

        if (waitScreen != null) {
            waitScreen.setVisibility(View.VISIBLE);
        }

        AccountCreator accountCreator = getAccountCreator();
        accountCreator.setActivationCode(mSmsCode.getText().toString());

        AccountCreator.Status status;
        if (mIsLinking) {
            status = accountCreator.activateAlias();
        } else if (mIsLogin) {
            status = accountCreator.loginLinphoneAccount();
        } else {
            status = accountCreator.activateAccount();
        }

        if (status != AccountCreator.Status.RequestOk) {

            Log.e(
                    "[Phone Account Validation] "
                            + (mIsLinking
                                    ? "linkAccount"
                                    : (mIsLogin ? "loginLinphoneAccount" : "activateAccount")
                                            + " returned ")
                            + status);
            showGenericErrorDialog(status);
            if (waitScreen != null) {
                waitScreen.setVisibility(View.GONE);
            }
            mFirebaseAnalytics.logEvent("account_sms_validation_false1", null);

            mSmsCode.getText().clear();
            mSmsCode.setEnabled(true);

            mSmsCode.clearFocus();
            mSmsCode.requestFocus();
            showSoftKeyboard(mSmsCode);
        } else {
            mFirebaseAnalytics.logEvent("account_sms_validation_ok", null);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assistant_phone_account_validation);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        waitScreen = findViewById(R.id.waitScreen);
        if (waitScreen != null) {
            waitScreen.setVisibility(View.GONE);
        }

        if (getIntent() != null && getIntent().getBooleanExtra("isLoginVerification", false)) {
            findViewById(R.id.title_account_login).setVisibility(View.VISIBLE);
            mIsLogin = true;
        } else if (getIntent() != null
                && getIntent().getBooleanExtra("isLinkingVerification", false)) {
            mIsLinking = true;
            findViewById(R.id.title_account_linking).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.title_account_creation).setVisibility(View.VISIBLE);
        }

        mActivationCodeLength =
                getResources().getInteger(R.integer.phone_number_validation_code_length);

        mPhoneNumber = findViewById(R.id.phone_number);
        mPhoneNumber.setText(getAccountCreator().getPhoneNumber());
        mPhoneNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        mSmsCode = findViewById(R.id.sms_code);
        mSmsCode.setFilters(
                new InputFilter[] {new InputFilter.LengthFilter(mActivationCodeLength)});

        mSmsCode.setLetterSpacing(0.4f);

        mSmsCode.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            getWindow()
                                    .setSoftInputMode(
                                            WindowManager.LayoutParams
                                                    .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            // showSoftKeyboard(v);
                            // mSmsCode.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        } else
                            getWindow()
                                    .setSoftInputMode(
                                            WindowManager.LayoutParams
                                                    .SOFT_INPUT_STATE_UNSPECIFIED);
                    }
                });

        mSmsCode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        // mFinishCreation.setEnabled(s.length() == mActivationCodeLength);
                        if (s.length() == mActivationCodeLength) {
                            smsCodeEntered();
                        }
                    }
                });

        // mFinishCreation = findViewById(R.id.finish_account_creation);
        // mFinishCreation.setEnabled(false);

        //        mFinishCreation.setOnClickListener(
        //                new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        Bundle bundle = new Bundle();
        //                        bundle.putString(
        //                                FirebaseAnalytics.Param.VALUE,
        // mSmsCode.getText().toString());
        //                        mFirebaseAnalytics.logEvent("phone_account_validation_click",
        // bundle);
        //
        //                        AccountCreator accountCreator = getAccountCreator();
        //                        mFinishCreation.setEnabled(false);
        //                        accountCreator.setActivationCode(mSmsCode.getText().toString());
        //
        //                        AccountCreator.Status status;
        //                        if (mIsLinking) {
        //                            status = accountCreator.activateAlias();
        //                        } else if (mIsLogin) {
        //                            status = accountCreator.loginLinphoneAccount();
        //                        } else {
        //                            status = accountCreator.activateAccount();
        //                        }
        //                        if (status != AccountCreator.Status.RequestOk) {
        //                            Log.e(
        //                                    "[Phone Account Validation] "
        //                                            + (mIsLinking
        //                                                    ? "linkAccount"
        //                                                    : (mIsLogin
        //                                                                    ?
        // "loginLinphoneAccount"
        //                                                                    : "activateAccount")
        //                                                            + " returned ")
        //                                            + status);
        //                            mFinishCreation.setEnabled(true);
        //                            showGenericErrorDialog(status);
        //                        }
        //                    }
        //                });

        mListener =
                new AccountCreatorListenerStub() {
                    @Override
                    public void onActivateAccount(
                            AccountCreator creator, AccountCreator.Status status, String resp) {
                        Log.i("[Phone Account Validation] onActivateAccount status is " + status);
                        if (status.equals(AccountCreator.Status.AccountActivated)) {
                            createProxyConfigAndLeaveAssistant();
                        } else {
                            onError(status);
                        }
                    }

                    @Override
                    public void onActivateAlias(
                            AccountCreator creator, AccountCreator.Status status, String resp) {
                        Log.i("[Phone Account Validation] onActivateAlias status is " + status);
                        if (status.equals(AccountCreator.Status.AccountActivated)) {
                            LinphonePreferences.instance().setLinkPopupTime("");
                            goToLinphoneActivity();
                        } else {
                            onError(status);
                        }
                    }

                    @Override
                    public void onLoginLinphoneAccount(
                            AccountCreator creator, AccountCreator.Status status, String resp) {
                        Log.i(
                                "[Phone Account Validation] onLoginLinphoneAccount status is "
                                        + status);
                        if (status.equals(AccountCreator.Status.RequestOk)) {
                            createProxyConfigAndLeaveAssistant();
                        } else {
                            onError(status);
                        }
                    }
                };

        mClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboard.addPrimaryClipChangedListener(
                new ClipboardManager.OnPrimaryClipChangedListener() {
                    @Override
                    public void onPrimaryClipChanged() {
                        ClipData data = mClipboard.getPrimaryClip();
                        if (data != null && data.getItemCount() > 0) {
                            String clip = data.getItemAt(0).getText().toString();
                            if (clip.length() == mActivationCodeLength) {
                                mSmsCode.setText(clip);
                            }
                        }
                    }
                });

        mSmsCode.clearFocus();
        mSmsCode.requestFocus();
        showSoftKeyboard(mSmsCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccountCreator().addListener(mListener);
        if (waitScreen != null) {
            waitScreen.setVisibility(View.GONE);
        }
        // Prevent user to go back, it won't be able to come back here after...
        // mBack.setEnabled(false);
        showSoftKeyboard(mSmsCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getAccountCreator().removeListener(mListener);
    }

    private void onError(AccountCreator.Status status) {
        // mFinishCreation.setEnabled(true);

        if (waitScreen != null) {
            waitScreen.setVisibility(View.GONE);
        }
        mFirebaseAnalytics.logEvent("account_sms_validation_false", null);
        showGenericErrorDialog(status);

        mSmsCode.getText().clear();
        mSmsCode.setEnabled(true);
        mSmsCode.clearFocus();
        mSmsCode.requestFocus();
        showSoftKeyboard(mSmsCode);

        if (status.equals(AccountCreator.Status.WrongActivationCode)) {
            // TODO do something so the server re-send a SMS
        }
    }

    public void showSoftKeyboard(View view) {
        // if (view.requestFocus()) {
        // InputMethodManager imm =
        //        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        // imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);

        view.dispatchTouchEvent(
                MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN,
                        0,
                        0,
                        0));
        view.dispatchTouchEvent(
                MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        0,
                        0,
                        0));
        // }
    }
}
