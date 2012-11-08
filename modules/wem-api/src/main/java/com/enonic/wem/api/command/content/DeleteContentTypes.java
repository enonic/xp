package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

public final class DeleteContentTypes
    extends Command<Integer>
{
    private QualifiedContentTypeNames contentTypeNames;

    public QualifiedContentTypeNames getNames()
    {
        return this.contentTypeNames;
    }

    public DeleteContentTypes names( final QualifiedContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteContentTypes ) )
        {
            return false;
        }

        final DeleteContentTypes that = (DeleteContentTypes) o;
        return Objects.equal( this.contentTypeNames, that.contentTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeNames, "Content type names cannot be null" );
    }
}
