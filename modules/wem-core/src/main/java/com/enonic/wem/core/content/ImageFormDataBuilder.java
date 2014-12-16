package com.enonic.wem.core.content;


import com.enonic.wem.api.data.PropertyTree;

final class ImageFormDataBuilder
{
    private String mimeType;

    private String image;

    ImageFormDataBuilder image( final String name )
    {
        this.image = name;
        return this;
    }

    ImageFormDataBuilder mimeType( final String mimeType )
    {
        this.mimeType = mimeType;
        return this;
    }

    void build( PropertyTree data )
    {
        data.setString( "media", image );
        data.setString( "mimeType", mimeType );
    }
}
