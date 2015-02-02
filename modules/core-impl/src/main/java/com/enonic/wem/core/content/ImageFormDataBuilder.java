package com.enonic.wem.core.content;

import com.enonic.wem.api.data.PropertyTree;

final class ImageFormDataBuilder
{
    private String image;

    ImageFormDataBuilder image( final String name )
    {
        this.image = name;
        return this;
    }

    void build( PropertyTree data )
    {
        data.setString( "media", image );
    }
}
