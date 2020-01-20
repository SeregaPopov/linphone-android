package org.linphone;

public class AccountManager {

    private String mCurrency = "Euro";
    private double mMoney = 14.76;

    public String MoneyString() {
        String tmp = mCurrency + ": " + String.valueOf(mMoney);
        return tmp;
    }

    public void UpdateMoneyRequest() {}

    // Запрос завершен. Обновляем информацию
    public void onMoneyRequestFinish() {}

    // Отправка запроса по таймеру

    // Отправка запроса после завершения звонка

}
