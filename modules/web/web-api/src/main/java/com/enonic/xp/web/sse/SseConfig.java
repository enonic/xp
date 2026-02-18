package com.enonic.xp.web.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SseConfig
{
    private Map<String, String> data = new ConcurrentHashMap<>();

    public Map<String, String> getData()
    {
        return this.data;
    }

    public void setData( final Map<String, String> data )
    {
        this.data = new ConcurrentHashMap<>( data );
    }
}
