package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;

public class GetContentByIds
    extends Command<Contents>
{
    private final ContentIds ids;

    public GetContentByIds( final ContentIds ids )
    {
        this.ids = ids;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( ids, "ids must be specified" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentByIds ) )
        {
            return false;
        }

        final GetContentByIds that = (GetContentByIds) o;
        return Objects.equal( this.ids, that.ids );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.ids );
    }

    public ContentIds getIds()
    {
        return this.ids;
    }
}
