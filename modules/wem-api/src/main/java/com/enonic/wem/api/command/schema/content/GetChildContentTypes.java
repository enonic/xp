package com.enonic.wem.api.command.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

public class GetChildContentTypes
    extends Command<ContentTypes>
{
    private ContentTypeName parentName;

    public ContentTypeName getParentName()
    {
        return parentName;
    }

    public GetChildContentTypes parentName( final ContentTypeName parentName )
    {
        this.parentName = parentName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetChildContentTypes ) )
        {
            return false;
        }

        final GetChildContentTypes that = (GetChildContentTypes) o;
        return Objects.equals( this.parentName, that.parentName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.parentName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.parentName, "Parent content type name cannot be null" );
    }
}
