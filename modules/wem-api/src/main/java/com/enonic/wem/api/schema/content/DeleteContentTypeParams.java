package com.enonic.wem.api.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

public final class DeleteContentTypeParams
{
    private ContentTypeName contentTypeName;

    public ContentTypeName getName()
    {
        return this.contentTypeName;
    }

    public DeleteContentTypeParams name( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteContentTypeParams ) )
        {
            return false;
        }

        final DeleteContentTypeParams that = (DeleteContentTypeParams) o;
        return Objects.equals( this.contentTypeName, that.contentTypeName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "Content type name cannot be null" );
    }
}
