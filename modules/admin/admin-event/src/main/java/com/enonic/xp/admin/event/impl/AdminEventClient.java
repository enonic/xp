package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * The browser client of the hub, served from {@link #PATH} of the api it connects to, so the
 * script reaches the socket by the host and path that reached the script.
 * <p>
 * Answered with an entity tag of the content and {@code no-cache}: the url does not change with
 * the content, so a client revalidates and is told the script is unchanged.
 */
final class AdminEventClient
{
    static final String PATH = "/client.js";

    private static final String RESOURCE = "/admin/event/client.js";

    private static final String CACHE_CONTROL = "no-cache";

    private final String content;

    private final String etag;

    AdminEventClient()
    {
        this.content = read();
        this.etag = etagOf( this.content );
    }

    WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder =
            WebResponse.create().header( HttpHeaders.ETAG, this.etag ).header( HttpHeaders.CACHE_CONTROL, CACHE_CONTROL );

        if ( this.etag.equals( request.getHeaders().get( HttpHeaders.IF_NONE_MATCH ) ) )
        {
            return responseBuilder.status( HttpStatus.NOT_MODIFIED ).build();
        }

        return responseBuilder.status( HttpStatus.OK ).contentType( MediaType.JAVASCRIPT_UTF_8 ).body( this.content ).build();
    }

    private static String read()
    {
        try (InputStream in = AdminEventClient.class.getResourceAsStream( RESOURCE ))
        {
            return new String( Objects.requireNonNull( in, RESOURCE ).readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static String etagOf( final String content )
    {
        try
        {
            final byte[] digest = MessageDigest.getInstance( "SHA-256" ).digest( content.getBytes( StandardCharsets.UTF_8 ) );
            return "\"" + HexFormat.of().formatHex( digest, 0, 16 ) + "\"";
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
