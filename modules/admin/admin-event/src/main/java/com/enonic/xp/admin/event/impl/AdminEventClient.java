package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * The browser client of the hub, served from {@link #PATH} of the api it connects to, so the
 * script reaches the socket by the host and path that reached the script.
 * <p>
 * Answered with an entity tag of the content and {@code no-cache}: the url does not change with
 * the content, so a client revalidates and is told the script is unchanged. The script is a
 * resource of this bundle, which cannot change while the bundle runs, so the tag is taken once.
 */
final class AdminEventClient
{
    static final String PATH = "/client.js";

    private static final String RESOURCE = "/admin/event/client.js";

    private static final String CACHE_CONTROL = "no-cache";

    private final Resource script;

    private final String etag;

    AdminEventClient()
    {
        this.script = new UrlResource( ResourceKey.from( ApplicationKey.SYSTEM, RESOURCE ), AdminEventClient.class.getResource( RESOURCE ) );
        this.etag = etagOf( this.script );
    }

    WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder =
            WebResponse.create().header( HttpHeaders.ETAG, this.etag ).header( HttpHeaders.CACHE_CONTROL, CACHE_CONTROL );

        if ( this.etag.equals( request.getHeaders().get( HttpHeaders.IF_NONE_MATCH ) ) )
        {
            return responseBuilder.status( HttpStatus.NOT_MODIFIED ).build();
        }

        return responseBuilder.status( HttpStatus.OK ).contentType( MediaType.JAVASCRIPT_UTF_8 ).body( this.script ).build();
    }

    private static String etagOf( final Resource resource )
    {
        try
        {
            final MessageDigest digest = MessageDigests.updateWithStream( MessageDigests.sha256(), resource::openStream );
            return "\"" + MessageDigests.formatHex( digest ) + "\"";
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
