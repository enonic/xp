package com.enonic.xp.core.impl.content;

import com.enonic.xp.data.PropertyTree;

final class ImageFormDataBuilder
{
    private String image;
    private String caption = "";
    private String artist = "";
    private String copyright = "";
    private String tags = "";

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

    void build( PropertyTree data )
    {
        data.setString( "media", image );
        data.setString( "caption", caption );
        data.setString( "artist", artist );
        data.setString( "copyright", copyright );
        data.setString( "tags", tags );
    }
}
