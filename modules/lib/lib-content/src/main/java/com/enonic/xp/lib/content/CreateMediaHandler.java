package com.enonic.xp.lib.content;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class CreateMediaHandler
    extends BaseContextHandler
{
    private String name;

    private String parentPath;

    private ByteSource data;

    private Double focalX;

    private Double focalY;

    private List<String> artist;

    private List<String> tags;

    private String caption;

    private String altText;

    private String copyright;

    private Supplier<String> idGenerator = () -> Long.toString( ThreadLocalRandom.current().nextLong( Long.MAX_VALUE ) );

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

    public void setFocalX( final double focalX )
    {
        this.focalX = focalX;
    }

    public void setFocalY( final double focalY )
    {
        this.focalY = focalY;
    }

    public void setArtist( final String[] artist )
    {
        this.artist = artist != null ? Arrays.asList( artist ) : null;
    }

    public void setTags( final String[] tags )
    {
        this.tags = tags != null ? Arrays.asList( tags ) : null;
    }

    public void setCaption( final String caption )
    {
        this.caption = caption;
    }

    public void setAltText( final String altText )
    {
        this.altText = altText;
    }

    public void setCopyright( final String copyright )
    {
        this.copyright = copyright;
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
        params.byteSource( this.data );
        if ( this.focalX != null || this.focalY != null )
        {
            params.focalPoint( new FocalPoint( this.focalX != null ? this.focalX : FocalPoint.DEFAULT.xOffset(),
                                               this.focalY != null ? this.focalY : FocalPoint.DEFAULT.yOffset() ) );
        }
        if ( this.artist != null )
        {
            params.artist( this.artist );
        }
        if ( this.tags != null )
        {
            params.tags( this.tags );
        }
        if ( this.caption != null )
        {
            params.caption( this.caption );
        }
        if ( this.altText != null )
        {
            params.altText( this.altText );
        }
        if ( this.copyright != null )
        {
            params.copyright( this.copyright );
        }
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
                final String fileName = Files.getNameWithoutExtension( baseName );
                final String ext = Files.getFileExtension( baseName );
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
