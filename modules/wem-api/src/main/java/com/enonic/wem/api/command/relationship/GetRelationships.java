package com.enonic.wem.api.command.relationship;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationships;

public class GetRelationships
    extends Command<Relationships>
{
    private ContentId fromContent;

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public GetRelationships fromContent( final ContentId fromContent )
    {
        this.fromContent = fromContent;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( fromContent, "fromContent cannot be null" );
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

        final GetRelationships that = (GetRelationships) o;

        return Objects.equals( fromContent, that.fromContent );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fromContent );
    }
}
