package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.UUIDPropertyIdProvider;

final class ImageFormDataBuilder
{
    private String image;

    private String caption = "";

    private String artist = "";

    private String copyright = "";

    private String tags = "";

    private double focalX = 0.5;

    private double focalY = 0.5;

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

    ImageFormDataBuilder focalX( final double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    ImageFormDataBuilder focalY( final double focalY )
    {
        this.focalY = focalY;
        return this;
    }

    void build( PropertyTree data )
    {
        Preconditions.checkArgument( focalX >= 0.0 && focalX <= 1.0, "Image focal point x value must be between 0 and 1 : %s", focalX );
        Preconditions.checkArgument( focalY >= 0.0 && focalY <= 1.0, "Image focal point y value must be between 0 and 1 : %s", focalY );

        PropertyTree tree = new PropertyTree( new UUIDPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, image );
        tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_X ), focalX );
        tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_Y ), focalY );

        data.setSet( ContentPropertyNames.MEDIA, tree.getRoot() );
        data.setString( "caption", caption );
        data.setString( "artist", artist );
        data.setString( "copyright", copyright );
        data.setString( "tags", tags );
    }
}
