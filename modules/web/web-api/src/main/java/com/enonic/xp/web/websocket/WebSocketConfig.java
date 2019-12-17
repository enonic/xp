package com.enonic.xp.web.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WebSocketConfig
{
    private List<String> subProtocols;

    private Map<String, String> data;

    public List<String> getSubProtocols()
    {
        return this.subProtocols != null ? this.subProtocols : new ArrayList<>();
    }

    public void setSubProtocols( final List<String> subProtocols )
    {
        this.subProtocols = subProtocols;
    }

    public Map<String, String> getData()
    {
        return this.data != null ? this.data : new HashMap<>();
    }

    public void setData( final Map<String, String> data )
    {
        this.data = data;
    }
}
