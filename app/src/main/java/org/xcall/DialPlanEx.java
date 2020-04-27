package org.xcall;

import org.linphone.core.DialPlan;

public class DialPlanEx implements DialPlan {

    private DialPlan _dialPlan = null;

    public DialPlanEx(DialPlan dialPlan) {
        if (dialPlan != null) {
            _dialPlan = dialPlan;
            _country = dialPlan.getCountry();
            _countryCallingCode = dialPlan.getCountryCallingCode();
            _internationCallPrefix = dialPlan.getInternationalCallPrefix();
            _isGeneric = dialPlan.isGeneric();
            _isoCountryCode = dialPlan.getIsoCountryCode();
            _nationalNumberLen = dialPlan.getNationalNumberLength();
        }
    }

    public DialPlanEx(
            String country,
            String isoCountryCode,
            String countryCallingCode,
            int nationalNumberLen,
            String internationCallPrefix) {
        _dialPlan = null;
        _country = country;
        _isoCountryCode = isoCountryCode;
        _countryCallingCode = countryCallingCode;
        _internationCallPrefix = internationCallPrefix;
        _nationalNumberLen = nationalNumberLen;
    }

    public DialPlan OrigDialPlan() {
        return _dialPlan;
    }

    String _country;

    @Override
    public String getCountry() {
        return _country;
    }

    String _countryCallingCode;

    @Override
    public String getCountryCallingCode() {
        return _countryCallingCode;
    }

    String _internationCallPrefix;

    @Override
    public String getInternationalCallPrefix() {
        return _internationCallPrefix;
    }

    boolean _isGeneric = false;

    @Override
    public boolean isGeneric() {
        return _isGeneric;
    }

    String _isoCountryCode;

    @Override
    public String getIsoCountryCode() {
        return _isoCountryCode;
    }

    int _nationalNumberLen = 0;

    @Override
    public int getNationalNumberLength() {
        return _nationalNumberLen;
    }

    @Override
    public DialPlan byCcc(String ccc) {
        if (_dialPlan != null) return _dialPlan.byCcc(ccc);
        return null;
    }

    @Override
    public DialPlan byCccAsInt(int ccc) {
        if (_dialPlan != null) return _dialPlan.byCccAsInt(ccc);
        return null;
    }

    @Override
    public DialPlan[] getAllList() {
        if (_dialPlan != null) return _dialPlan.getAllList();
        return new DialPlan[0];
    }

    @Override
    public int lookupCccFromE164(String e164) {
        if (_dialPlan != null) return _dialPlan.lookupCccFromE164(e164);
        return 0;
    }

    @Override
    public int lookupCccFromIso(String iso) {
        if (_dialPlan != null) return _dialPlan.lookupCccFromIso(iso);
        return 0;
    }

    @Override
    public void setUserData(Object data) {
        if (_dialPlan != null) _dialPlan.setUserData(data);
    }

    @Override
    public Object getUserData() {
        if (_dialPlan != null) return _dialPlan.getUserData();
        return null;
    }
}
