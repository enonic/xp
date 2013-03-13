package com.enonic.wem.api.command.content.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;

public final class GetContentTypes
    extends Command<ContentTypes>
{
    private QualifiedContentTypeNames qualifiedNames;

    private boolean getAllContentTypes = false;

    private boolean mixinReferencesToFormItems = false;

    public QualifiedContentTypeNames getQualifiedNames()
    {
        return this.qualifiedNames;
    }

    public GetContentTypes qualifiedNames( final QualifiedContentTypeNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
        return this;
    }

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetContentTypes mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
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
        return Objects.equal( this.qualifiedNames, that.qualifiedNames ) && ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.qualifiedNames == null, "Cannot specify both get all and get content type names" );
        }
        else
        {
            Preconditions.checkNotNull( this.qualifiedNames, "qualifiedNames cannot be null" );
        }
    }

}
