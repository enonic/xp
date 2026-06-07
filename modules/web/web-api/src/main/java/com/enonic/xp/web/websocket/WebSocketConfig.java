package com.enonic.xp.web.websocket;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class WebSocketConfig
{
    public static final Duration DEFAULT_SESSION_ACCESS_THROTTLE = Duration.ofSeconds( 60 );

    private List<String> subProtocols = List.of();

    private Map<String, String> data = new ConcurrentHashMap<>();

    private Predicate<String> originValidator;

    private boolean terminateOnSessionExit = true;

    private boolean sessionAccess = false;

    private Duration sessionAccessThrottle = DEFAULT_SESSION_ACCESS_THROTTLE;

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

    public boolean isTerminateOnSessionExit()
    {
        return this.terminateOnSessionExit;
    }

    public void setTerminateOnSessionExit( final boolean terminateOnSessionExit )
    {
        this.terminateOnSessionExit = terminateOnSessionExit;
    }

    public boolean isSessionAccess()
    {
        return this.sessionAccess;
    }

    public void setSessionAccess( final boolean sessionAccess )
    {
        this.sessionAccess = sessionAccess;
    }

    public Duration getSessionAccessThrottle()
    {
        return this.sessionAccessThrottle;
    }

    public void setSessionAccessThrottle( final Duration sessionAccessThrottle )
    {
        this.sessionAccessThrottle = sessionAccessThrottle == null ? DEFAULT_SESSION_ACCESS_THROTTLE : sessionAccessThrottle;
    }
}
