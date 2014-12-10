package com.enonic.wem.core.content;


import com.enonic.wem.api.data.PropertyTree;

final class ImageFormDataBuilder
{
    private String mimeType;

    private String name;

    ImageFormDataBuilder setName( final String name )
    {
        this.name = name;
        return this;
    }

    ImageFormDataBuilder setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
        return this;
    }

    void build( PropertyTree data )
    {
        data.setString( "mimeType", mimeType );
        data.setString( "image", name );
    }
}
