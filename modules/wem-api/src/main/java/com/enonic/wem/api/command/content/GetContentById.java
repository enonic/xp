package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class GetContentById
    extends Command<Content>
{
    private final ContentId id;

    public GetContentById( final ContentId id )
    {
        this.id = id;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be specified" );
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

        final GetContentById that = (GetContentById) o;

        if ( !id.equals( that.id ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public ContentId getId()
    {
        return this.id;
    }
}
