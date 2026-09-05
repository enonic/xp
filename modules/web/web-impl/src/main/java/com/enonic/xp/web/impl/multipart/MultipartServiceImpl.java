package com.enonic.xp.web.impl.multipart;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.MediaType;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

@Component
public final class MultipartServiceImpl
    implements MultipartService
{
    private static final MediaType MULTIPART_FORM = MediaType.create( "multipart", "form-data" );

    private static final String BOUNDARY = "boundary";

    @Override
    public MultipartForm parse( final HttpServletRequest req )
    {
        return new MultipartFormImpl( getParts( req ) );
    }

    private static MediaType getMediaType( final HttpServletRequest req )
    {
        final String value = req.getContentType();
        if ( value == null )
        {
            return MediaType.OCTET_STREAM;
        }
        try
        {
            return MediaType.parse( value );
        }
        catch ( IllegalArgumentException e )
        {
            return MediaType.OCTET_STREAM;
        }
    }

    private static Iterable<Part> getParts( final HttpServletRequest req )
    {
        final MediaType type = getMediaType( req );
        if ( !type.withoutParameters().is( MULTIPART_FORM ) )
        {
            return Collections.emptyList();
        }
        if ( !type.parameters().containsKey( BOUNDARY ) )
        {
            throw WebException.badRequest( "Missing multipart boundary" );
        }

        try
        {
            return req.getParts();
        }
        catch ( IllegalStateException e )
        {
            throw new WebException( HttpStatus.PAYLOAD_TOO_LARGE, "Multipart request exceeds the configured limits", e );
        }
        catch ( IOException | IllegalArgumentException e )
        {
            throw WebException.badRequest( "Malformed multipart request", e );
        }
        catch ( ServletException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
