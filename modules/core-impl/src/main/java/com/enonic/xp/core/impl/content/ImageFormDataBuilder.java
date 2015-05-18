package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;

final class ImageFormDataBuilder
{
    private String image;

    private String caption = "";

    private String artist = "";

    private String copyright = "";

    private String tags = "";

    private Double focalX;

    private Double focalY;

    ImageFormDataBuilder image( final String name )
    {
        this.image = name;
        return this;
    }

    ImageFormDataBuilder caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    ImageFormDataBuilder artist( final String artist )
    {
        this.artist = artist;
        return this;
    }

    ImageFormDataBuilder copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    ImageFormDataBuilder tags( final String tags )
    {
        this.tags = tags;
        return this;
    }

    ImageFormDataBuilder focalX( final Double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    ImageFormDataBuilder focalY( final Double focalY )
    {
        this.focalY = focalY;
        return this;
    }

    void build( PropertyTree data )
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.DefaultPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, image );
        if ( focalX != null )
        {
            tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_X ), focalX );
        }
        if ( focalY != null )
        {
            tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_Y ), focalY );
        }

        data.setSet( ContentPropertyNames.MEDIA, tree.getRoot() );
        data.setString( "caption", caption );
        data.setString( "artist", artist );
        data.setString( "copyright", copyright );
        data.setString( "tags", tags );
    }
}
