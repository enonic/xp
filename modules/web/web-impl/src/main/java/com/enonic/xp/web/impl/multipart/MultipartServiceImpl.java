package com.enonic.xp.web.impl.multipart;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.MediaType;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

@Component
public final class MultipartServiceImpl
    implements MultipartService
{
    private final static MediaType MULTIPART_FORM = MediaType.create( "multipart", "form-data" );

    @Override
    public MultipartForm parse( final HttpServletRequest req )
    {
        return new MultipartFormImpl( getParts( req ) );
    }

    private MediaType getMediaType( final HttpServletRequest req )
    {
        final String value = req.getContentType();
        return value != null ? MediaType.parse( value ) : MediaType.OCTET_STREAM;
    }

    private Iterable<Part> getParts( final HttpServletRequest req )
    {
        final MediaType type = getMediaType( req ).withoutParameters();
        if ( !type.is( MULTIPART_FORM ) )
        {
            return Collections.emptyList();
        }

        try
        {
            return req.getParts();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
