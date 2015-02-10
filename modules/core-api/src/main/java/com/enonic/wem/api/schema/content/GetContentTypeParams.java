package com.enonic.wem.api.schema.content;

import com.google.common.base.Preconditions;

public class GetContentTypeParams
{
    private ContentTypeName contentTypeName;

    private boolean inlinesToFormItems = false;

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

    public boolean isInlinesToFormItems()
    {
        return inlinesToFormItems;
    }

    public GetContentTypeParams inlinesToFormItems( final boolean value )
    {
        inlinesToFormItems = value;
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

        if ( inlinesToFormItems != that.inlinesToFormItems )
        {
            return false;
        }
        if ( !contentTypeName.equals( that.contentTypeName ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentTypeName.hashCode();
        result = 31 * result + ( inlinesToFormItems ? 1 : 0 );
        return result;
    }
}
