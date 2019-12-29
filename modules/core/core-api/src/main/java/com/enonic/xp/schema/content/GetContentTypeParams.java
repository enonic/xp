package com.enonic.xp.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class GetContentTypeParams
{
    private ContentTypeName contentTypeName;

    public static GetContentTypeParams from( final ContentTypeName contentTypeName )
    {
        return new GetContentTypeParams().contentTypeName( contentTypeName );
    }

    public ContentTypeName getContentTypeName()
    {
        return this.contentTypeName;
    }

    public GetContentTypeParams contentTypeName( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    public GetContentTypeParams contentTypeName( final String value )
    {
        this.contentTypeName = ContentTypeName.from( value );
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "contentTypeName cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final GetContentTypeParams that = (GetContentTypeParams) o;

        return contentTypeName.equals( that.contentTypeName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentTypeName );
    }
}
