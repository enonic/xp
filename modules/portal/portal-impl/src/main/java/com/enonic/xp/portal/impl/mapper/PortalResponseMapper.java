package com.enonic.xp.portal.impl.mapper;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class PortalResponseMapper
    implements MapSerializable
{
    private static volatile boolean lowercaseHeaders = true;

    private final PortalResponse response;

    public PortalResponseMapper( final PortalResponse response )
    {
        this.response = response;
    }

    static void setLowercaseHeaders( final boolean lowercaseHeaders )
    {
        PortalResponseMapper.lowercaseHeaders = lowercaseHeaders;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "status", this.response.getStatus().value() );
        gen.value( "contentType", this.response.getContentType() );
        gen.value( "postProcess", this.response.isPostProcess() );

        serializeHeaders( gen );

        serializePageContributions( gen );
        serializeCookies( gen );
        gen.value( "applyFilters", this.response.applyFilters() );
        gen.value( "body", this.response.getBody() );
    }

    private void serializeHeaders( final MapGenerator gen )
    {
        final Map<String, String> headers = this.response.getHeaders();
        gen.value( "headers", headers );
        if ( lowercaseHeaders )
        {
            gen.map( "headers" );
            headers.forEach( ( key, value ) -> gen.value( key.toLowerCase( Locale.ROOT ), value ) );
            gen.end();
        }
        else
        {
            gen.value( "headers", headers );
        }
    }

    private void serializePageContributions( final MapGenerator gen )
    {
        gen.map( "pageContributions" );
        for ( final HtmlTag tag : HtmlTag.values() )
        {
            final ImmutableList<String> contributions = response.getContributions( tag );
            if ( contributions.isEmpty() )
            {
                continue;
            }

            gen.array( tag.id() );
            contributions.forEach( gen::value );
            gen.end();
        }
        gen.end();
    }

    private void serializeCookies( final MapGenerator gen )
    {
        final ImmutableList<Cookie> cookies = response.getCookies();
        if ( cookies.isEmpty() )
        {
            return;
        }
        gen.map( "cookies" );
        for ( final Cookie cookie : cookies )
        {
            gen.map( cookie.getName() );
            if ( cookie.getValue() != null )
            {
                gen.value( "value", cookie.getValue() );
            }
            if ( cookie.getPath() != null )
            {
                gen.value( "path", cookie.getPath() );
            }
            if ( cookie.getDomain() != null )
            {
                gen.value( "domain", cookie.getDomain() );
            }
            if ( cookie.getComment() != null )
            {
                gen.value( "comment", cookie.getComment() );
            }
            if ( cookie.getMaxAge() >= 0 )
            {
                gen.value( "maxAge", cookie.getMaxAge() );
            }
            if ( cookie.getSecure() )
            {
                gen.value( "secure", cookie.getSecure() );
            }
            if ( cookie.isHttpOnly() )
            {
                gen.value( "httpOnly", cookie.isHttpOnly() );
            }
            gen.end();
        }
        gen.end();
    }
}
