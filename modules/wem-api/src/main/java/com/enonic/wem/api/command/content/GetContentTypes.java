package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;

public final class GetContentTypes
    extends Command<ContentTypes>
{
    private QualifiedContentTypeNames contentTypeNames;

    private boolean getAllContentTypes = false;

    public QualifiedContentTypeNames getNames()
    {
        return this.contentTypeNames;
    }

    public GetContentTypes names( final QualifiedContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    public boolean isGetAll()
    {
        return getAllContentTypes;
    }

    public GetContentTypes all()
    {
        getAllContentTypes = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentTypes ) )
        {
            return false;
        }

        final GetContentTypes that = (GetContentTypes) o;
        return Objects.equal( this.contentTypeNames, that.contentTypeNames ) && ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.contentTypeNames == null, "Cannot specify both get all and get content type names" );
        }
        else
        {
            Preconditions.checkNotNull( this.contentTypeNames, "Content type cannot be null" );
        }
    }

}
