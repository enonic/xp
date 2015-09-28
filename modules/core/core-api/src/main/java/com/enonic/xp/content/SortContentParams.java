package com.enonic.xp.content;


import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@Beta
public final class SortContentParams
{
    private ContentId contentId;

    public SortContentParams()
    {
    }

    public SortContentParams contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof SortContentParams ) )
        {
            return false;
        }

        final SortContentParams that = (SortContentParams) o;
        return Objects.equal( this.contentId, that.contentId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
    }
}
