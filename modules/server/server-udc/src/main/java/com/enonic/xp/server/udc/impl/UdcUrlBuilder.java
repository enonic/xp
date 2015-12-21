package com.enonic.xp.server.udc.impl;

import java.net.URL;
import java.util.Map;

import com.google.common.base.Joiner;

public final class UdcUrlBuilder
{
    private final String baseUrl;

    public UdcUrlBuilder( final String )
    public URL generate( final UdcInfo info )
    {
        final Map<String, String> map = toMap(info);
        final String params = Joiner.on( '&' ).withKeyValueSeparator( "=" ).join( map );



    }
}
