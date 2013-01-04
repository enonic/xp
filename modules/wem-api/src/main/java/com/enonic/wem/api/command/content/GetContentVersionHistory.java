package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersionHistory;

public final class GetContentVersionHistory
    extends Command<ContentVersionHistory>
{
    private ContentSelector selector;

    public ContentSelector getSelector()
    {
        return this.selector;
    }

    public GetContentVersionHistory selector( final ContentSelector selector )
    {
        this.selector = selector;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentVersionHistory ) )
        {
            return false;
        }

        final GetContentVersionHistory that = (GetContentVersionHistory) o;
        return Objects.equal( this.selector, that.selector );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.selector );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Content selector cannot be null" );
    }
}
