package com.enonic.xp.lib.content;

import java.util.Arrays;
import java.util.List;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public class UpdateMediaHandler
    extends BaseContextHandler
{
    private String key;

    private String name;

    private ByteSource data;

    private Double focalX;

    private Double focalY;

    private String caption;

    private List<String> artist;

    private String copyright;

    private List<String> tags;

    @Override
    protected ContentMapper doExecute()
    {
        final UpdateMediaParams params = new UpdateMediaParams();
        params.content( getContentId( this.key ) );
        params.name( name );
        params.byteSource( data );
        if ( focalX != null || focalY != null )
        {
            params.focalPoint( new FocalPoint( focalX != null ? focalX : FocalPoint.DEFAULT.xOffset(),
                                               focalY != null ? focalY : FocalPoint.DEFAULT.yOffset() ) );
        }
        params.caption( caption );
        params.artist( artist );
        params.copyright( copyright );
        params.tags( tags );

        final Content result = this.contentService.update( params );
        return new ContentMapper( result );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setName( final String name )
    {
        this.name = name;
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

    public void setCaption( final String caption )
    {
        this.caption = caption;
    }

    public void setArtist( final String[] artist )
    {
        this.artist = artist != null ? Arrays.asList( artist ) : null;
    }

    public void setCopyright( final String copyright )
    {
        this.copyright = copyright;
    }

    public void setTags( final String[] tags )
    {
        this.tags = tags != null ? Arrays.asList( tags ) : null;
    }

}
