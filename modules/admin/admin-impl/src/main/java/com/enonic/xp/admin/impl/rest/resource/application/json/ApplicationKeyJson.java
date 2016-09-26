package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.app.ApplicationKey;

public final class ApplicationKeyJson
{
    private final String key;

    public ApplicationKeyJson( final ApplicationKey applicationKey )
    {
        this.key = applicationKey.toString();
    }

    public String getKey()
    {
        return key;
    }
}
