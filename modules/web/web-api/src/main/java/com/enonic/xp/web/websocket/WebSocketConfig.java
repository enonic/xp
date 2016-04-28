package com.enonic.xp.web.websocket;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class WebSocketConfig
{
    private List<String> subProtocols;

    private Map<String, String> data;

    public List<String> getSubProtocols()
    {
        return this.subProtocols != null ? this.subProtocols : Lists.newArrayList();
    }

    public void setSubProtocols( final List<String> subProtocols )
    {
        this.subProtocols = subProtocols;
    }

    public Map<String, String> getData()
    {
        return this.data != null ? this.data : Maps.newHashMap();
    }

    public void setData( final Map<String, String> data )
    {
        this.data = data;
    }
}
