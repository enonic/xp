package com.enonic.cms.web.rest.account;

import java.util.ArrayList;
import java.util.List;

public final class AccountExportRequest extends AccountLoadRequest
{

    private List<String> keys;

    public AccountExportRequest()
    {
        keys = new ArrayList<String>(  );
    }

    public List<String> getKeys()
    {
        return keys;
    }

    public void setKeys( List<String> keys )
    {
        this.keys = keys;
    }

}