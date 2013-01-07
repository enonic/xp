package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersionId;

public final class GetContentVersion
    extends Command<Content>
{
    private ContentSelector selector;

    private ContentVersionId version;

    public ContentSelector getSelector()
    {
        return this.selector;
    }

    public ContentVersionId getVersion()
    {
        return version;
    }

    public GetContentVersion selector( final ContentSelector selector )
    {
        this.selector = selector;
        return this;
    }

    public GetContentVersion version( final ContentVersionId version )
    {
        this.version = version;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentVersion ) )
        {
            return false;
        }

        final GetContentVersion that = (GetContentVersion) o;
        return Objects.equal( this.selector, that.selector ) && Objects.equal( this.version, that.version );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.selector, this.version );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Content selector cannot be null" );
        Preconditions.checkNotNull( this.version, "Content version cannot be null" );
    }
}
