package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class UndoPendingDeleteContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private UndoPendingDeleteContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
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
        final UndoPendingDeleteContentParams that = (UndoPendingDeleteContentParams) o;
        return Objects.equals( contentIds, that.contentIds ) && Objects.equals( target, that.target );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentIds, target );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
        }

        public UndoPendingDeleteContentParams build()
        {
            return new UndoPendingDeleteContentParams( this );
        }
    }
}
