package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class CreateContentType
    extends Command<QualifiedContentTypeName>
{
    private ContentType contentType;

    public CreateContentType contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateContentType ) )
        {
            return false;
        }

        final CreateContentType that = (CreateContentType) o;
        return Objects.equal( this.contentType, that.contentType );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentType );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentType, "Content type cannot be null" );
    }
}
