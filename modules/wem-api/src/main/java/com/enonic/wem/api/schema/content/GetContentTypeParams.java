package com.enonic.wem.api.schema.content;

import com.google.common.base.Preconditions;

public class GetContentTypeParams
{
    private ContentTypeName contentTypeName;

    private boolean mixinReferencesToFormItems = false;

    private boolean notFoundAsException = false;

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

    public GetContentTypeParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetContentTypeParams notFoundAsNull()
    {
        notFoundAsException = false;
        return this;
    }

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetContentTypeParams mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "contentTypeName cannot be null" );
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
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

        if ( mixinReferencesToFormItems != that.mixinReferencesToFormItems )
        {
            return false;
        }
        if ( notFoundAsException != that.notFoundAsException )
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
        result = 31 * result + ( mixinReferencesToFormItems ? 1 : 0 );
        result = 31 * result + ( notFoundAsException ? 1 : 0 );
        return result;
    }
}
