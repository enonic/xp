package com.enonic.xp.portal.impl.mapper;

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
    private final PortalResponse response;

    public PortalResponseMapper( final PortalResponse response )
    {
        this.response = response;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "status", this.response.getStatus().value() );
        gen.value( "contentType", this.response.getContentType() );
        gen.value( "postProcess", this.response.isPostProcess() );

        MapperHelper.serializeMap( "headers", gen, this.response.getHeaders() );
        serializePageContributions( gen );
        serializeCookies( gen );
        gen.value( "applyFilters", this.response.applyFilters() );
        serializeBody( gen );
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
            else if ( cookie.getPath() != null )
            {
                gen.value( "path", cookie.getPath() );
            }
            else if ( cookie.getDomain() != null )
            {
                gen.value( "domain", cookie.getDomain() );
            }
            else if ( cookie.getComment() != null )
            {
                gen.value( "comment", cookie.getComment() );
            }
            else if ( cookie.getMaxAge() != -1 )
            {
                gen.value( "maxAge", cookie.getMaxAge() );
            }
            else if ( cookie.getSecure() )
            {
                gen.value( "secure", cookie.getSecure() );
            }
            else if ( cookie.isHttpOnly() )
            {
                gen.value( "httpOnly", cookie.isHttpOnly() );
            }
            gen.end();
        }
        gen.end();
    }

    private void serializeBody( final MapGenerator gen )
    {
        final Object body = this.response.getBody();
        if ( body instanceof Map )
        {
            MapperHelper.serializeMap( "body", gen, (Map) body, true );
        }
        else
        {
            gen.value( "body", body );
        }
    }
}
