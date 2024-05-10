package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.schema.content.ContentTypeName;

final class MediaFormDataBuilder
{
    private ContentTypeName type;

    private String attachment;

    private String caption = "";

    private String altText = "";

    private List<String> artist;

    private String copyright = "";

    private List<String> tags;

    private double focalX = 0.5;

    private double focalY = 0.5;

    public MediaFormDataBuilder type( final ContentTypeName type )
    {
        this.type = type;
        return this;
    }

    MediaFormDataBuilder attachment( final String name )
    {
        this.attachment = name;
        return this;
    }

    MediaFormDataBuilder caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    MediaFormDataBuilder altText( final String altText )
    {
        this.altText = altText;
        return this;
    }

    MediaFormDataBuilder artist( final List<String> artist )
    {
        this.artist = artist;
        return this;
    }

    MediaFormDataBuilder copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    MediaFormDataBuilder tags( final List<String> tags )
    {
        this.tags = tags;
        return this;
    }

    MediaFormDataBuilder focalX( final double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    MediaFormDataBuilder focalY( final double focalY )
    {
        this.focalY = focalY;
        return this;
    }

    void build( PropertyTree data )
    {
        Preconditions.checkNotNull( type, "Type cannot be null." );
        Preconditions.checkArgument( focalX >= 0.0 && focalX <= 1.0, "Image focal point x value must be between 0 and 1 : %s", focalX );
        Preconditions.checkArgument( focalY >= 0.0 && focalY <= 1.0, "Image focal point y value must be between 0 and 1 : %s", focalY );

        final PropertySet set = data.newSet();
        set.setString( ContentPropertyNames.MEDIA_ATTACHMENT, attachment );

        if ( type.isImageMedia() )
        {
            set.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_X ), focalX );
            set.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_Y ), focalY );
        }

        data.removeProperties( ContentPropertyNames.MEDIA );
        data.setSet( ContentPropertyNames.MEDIA, set );

        if ( !type.isDocumentMedia() )
        {
            if ( caption != null )
            {
                data.setString( "caption", caption );
            }
            if ( artist != null )
            {
                data.setValues( "artist", artist.stream().map( ValueFactory::newString ).collect( Collectors.toList() ) );
            }
            if ( copyright != null )
            {
                data.setString( "copyright", copyright );
            }
            if ( tags != null )
            {
                data.setValues( "tags", tags.stream().map( ValueFactory::newString ).collect( Collectors.toList() ) );
            }
            if ( altText != null )
            {
                data.setString( "altText", altText );
            }
        }
    }
}
