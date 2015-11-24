package com.enonic.xp.lib.content;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class CreateMediaHandler
    extends BaseContextHandler
{
    private final static Random RANDOM = new SecureRandom();

    private String name;

    private String parentPath;

    private ByteSource data;

    private String mimeType;

    private double focalX = 0.5;

    private double focalY = 0.5;

    private Supplier<String> idGenerator = () -> Long.toString( Math.abs( RANDOM.nextLong() ) );

    @Override
    protected Object doExecute()
    {
        String name = this.name;
        Content result = null;
        final ContentPath parent = this.parentPath != null ? ContentPath.from( this.parentPath ) : ContentPath.ROOT;

        while ( result == null )
        {
            final CreateMediaParams params = createParams( name );
            try
            {
                result = this.contentService.create( params );
            }
            catch ( ContentAlreadyExistsException e )
            {
                name = generateUniqueContentName( this.idGenerator, parent, this.name );
            }
        }
        return new ContentMapper( result );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setData( final ByteSource data )
    {
        this.data = data;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public void setFocalX( final double focalX )
    {
        this.focalX = focalX;
    }

    public void setFocalY( final double focalY )
    {
        this.focalY = focalY;
    }

    public void setIdGenerator( final Supplier<String> idGenerator )
    {
        if ( idGenerator != null )
        {
            this.idGenerator = idGenerator;
        }
    }

    private CreateMediaParams createParams( final String name )
    {
        final CreateMediaParams params = new CreateMediaParams();
        params.name( name );
        params.parent( this.parentPath != null ? ContentPath.from( this.parentPath ) : null );
        params.mimeType( this.mimeType );
        params.byteSource( this.data );
        params.focalX( this.focalX );
        params.focalY( this.focalY );
        return params;
    }

    protected String generateUniqueContentName( final Supplier<String> idGenerator, final ContentPath parent, final String baseName )
    {
        String name = baseName;
        while ( this.contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            final String randomId = idGenerator.get();
            if ( baseName.contains( "." ) )
            {
                final String fileName = StringUtils.substringBeforeLast( baseName, "." );
                final String ext = StringUtils.substringAfterLast( baseName, "." );
                name = fileName + "-" + randomId + "." + ext;
            }
            else
            {
                name = baseName + "-" + randomId;
            }

        }
        return name;
    }

}
