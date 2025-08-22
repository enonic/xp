package com.enonic.xp.issue;

import java.util.Objects;

import com.enonic.xp.content.ContentId;

public final class PublishRequestItem
{
    private final ContentId id;

    private final boolean includeChildren;

    private PublishRequestItem( Builder builder )
    {
        this.id = builder.id;
        this.includeChildren = builder.includeChildren;
    }

    public ContentId getId()
    {
        return id;
    }

    public boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
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

        final PublishRequestItem that = (PublishRequestItem) o;

        return Objects.equals( id, that.id ) && this.includeChildren == that.includeChildren;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, includeChildren );
    }

    public static final class Builder
    {
        private ContentId id;

        private boolean includeChildren = false;

        private Builder()
        {
        }

        public Builder id( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren )
        {
            if ( includeChildren != null )
            {
                this.includeChildren = includeChildren;
            }
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( id, "content id is required" );
        }

        public PublishRequestItem build()
        {
            validate();
            return new PublishRequestItem( this );
        }
    }
}
