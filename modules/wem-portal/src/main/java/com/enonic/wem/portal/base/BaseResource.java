package com.enonic.wem.portal.base;

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

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.rendering.RenderResult;

public abstract class BaseResource
    extends ServerResource
{
    protected RenderingMode mode;

    @Override
    protected void doInit()
        throws ResourceException
    {
        final String modeStr = getAttribute( "mode" );
        this.mode = RenderingMode.from( modeStr );
        if ( this.mode == null )
        {
            throw illegalMode( modeStr );
        }
    }

    private ResourceException illegalMode( final String mode )
    {
        final String validModes = Joiner.on( "," ).join( RenderingMode.values() ).toLowerCase();
        return notFound( "Illegal mode [%s]. Should be one of [%s].", mode, validModes );
    }

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

        final Series<Header> headers = getResponse().getHeaders();
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
