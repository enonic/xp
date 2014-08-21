package com.enonic.wem.portal.internal.base;

import java.util.Arrays;
import java.util.Map;

import org.restlet.data.Form;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.restlet.RestletUtils;

public abstract class BaseResource
    extends ServerResource
{

    protected final ResourceException notFound( final String message, final Object... args )
    {
        return new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, String.format( message, args ) );
    }

    protected final Multimap<String, String> getParams()
    {
        final Form form = getReference().getQueryAsForm();
        final Multimap<String, String> params = HashMultimap.create();

        for ( final String name : form.getNames() )
        {
            params.putAll( name, Arrays.asList( form.getValuesArray( name ) ) );
        }

        return params;
    }

    protected final Representation toRepresentation( final RenderResult result )
    {
        getResponse().setStatus( Status.valueOf( result.getStatus() ) );

        final Series<Header> headers = RestletUtils.getHeaders( getResponse(), true );
        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            headers.set( header.getKey(), header.getValue() );
        }

        final MediaType type = MediaType.valueOf( result.getType() );
        if ( result.getEntity() instanceof byte[] )
        {
            return new ByteArrayRepresentation( (byte[]) result.getEntity(), type );
        }
        else
        {
            return new StringRepresentation( result.getAsString(), type );
        }
    }
}
