package com.enonic.xp.web.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class WebSocketConfig
{
    private List<String> subProtocols = List.of();

    private Map<String, String> data = new ConcurrentHashMap<>();

    private Predicate<String> originValidator;

    public List<String> getSubProtocols()
    {
        return this.subProtocols;
    }

    public void setSubProtocols( final List<String> subProtocols )
    {
        this.subProtocols = List.copyOf( subProtocols );
    }

    public Map<String, String> getData()
    {
        return this.data;
    }

    public void setData( final Map<String, String> data )
    {
        this.data = new ConcurrentHashMap<>( data );
    }

    public Predicate<String> getOriginValidator()
    {
        return this.originValidator;
    }

    public void setOriginValidator( final Predicate<String> originValidator )
    {
        this.originValidator = originValidator;
    }
}
