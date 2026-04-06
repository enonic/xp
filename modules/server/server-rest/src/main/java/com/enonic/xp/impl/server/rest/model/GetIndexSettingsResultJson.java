package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

public final class GetIndexSettingsResultJson
{
    public Map<String, String> settings;

    public static GetIndexSettingsResultJson create( final Map<String, String> settings )
    {
        final GetIndexSettingsResultJson json = new GetIndexSettingsResultJson();
        json.settings = settings;
        return json;
    }
}
