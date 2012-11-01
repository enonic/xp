package com.enonic.wem.api.command.content;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class GetContentTypes
    extends Command<ContentTypes>
{
    private List<QualifiedContentTypeName> contentTypeNames = ImmutableList.of();

    public List<QualifiedContentTypeName> getNames()
    {
        return this.contentTypeNames;
    }

    public GetContentTypes names( final QualifiedContentTypeName... contentTypeNames )
    {
        this.contentTypeNames = ImmutableList.copyOf( contentTypeNames );
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
        Preconditions.checkArgument( !this.contentTypeNames.isEmpty(), "Content type names must be specified" );
    }
}
