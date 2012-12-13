package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;

public final class GetContents
    extends Command<Contents>
{
    private ContentSelectors selectors;

    public ContentSelectors getSelectors()
    {
        return this.selectors;
    }

    public GetContents selectors( final ContentSelectors selectors )
    {
        this.selectors = selectors;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContents ) )
        {
            return false;
        }

        final GetContents that = (GetContents) o;
        return Objects.equal( this.selectors, that.selectors );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.selectors );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selectors, "Content selectors cannot be null" );
    }
}
