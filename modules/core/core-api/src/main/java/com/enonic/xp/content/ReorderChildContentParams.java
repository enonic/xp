package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import static java.util.Objects.requireNonNull;


public final class ReorderChildContentParams
{
    private final ContentId contentToMove;

    private final ContentId contentToMoveBefore;

    private final String afterOrderKey;

    private final String beforeOrderKey;

    private ReorderChildContentParams( final Builder builder )
    {
        contentToMove = builder.contentToMove;
        contentToMoveBefore = builder.contentToMoveBefore;
        afterOrderKey = builder.afterOrderKey;
        beforeOrderKey = builder.beforeOrderKey;
    }

    public ContentId getContentToMove()
    {
        return contentToMove;
    }

    public ContentId getContentToMoveBefore()
    {
        return contentToMoveBefore;
    }

    /**
     * The order key of the sibling shown directly above the drop point - the moved content lands after it. With
     * {@link #getBeforeOrderKey()} also set, it lands between the two. Neither set means the top of the list.
     */
    public String getAfterOrderKey()
    {
        return afterOrderKey;
    }

    /**
     * The order key of the sibling shown directly below the drop point - the moved content lands before it.
     */
    public String getBeforeOrderKey()
    {
        return beforeOrderKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentId contentToMove;

        private ContentId contentToMoveBefore;

        private String afterOrderKey;

        private String beforeOrderKey;

        private Builder()
        {
        }

        public Builder contentToMove( ContentId contentToMove )
        {
            this.contentToMove = contentToMove;
            return this;
        }

        public Builder contentToMoveBefore( ContentId contentToMoveBefore )
        {
            this.contentToMoveBefore = contentToMoveBefore;
            return this;
        }

        public Builder afterOrderKey( final String afterOrderKey )
        {
            this.afterOrderKey = afterOrderKey;
            return this;
        }

        public Builder beforeOrderKey( final String beforeOrderKey )
        {
            this.beforeOrderKey = beforeOrderKey;
            return this;
        }

        public ReorderChildContentParams build()
        {
            requireNonNull( contentToMove, "contentToMove is required" );
            Preconditions.checkArgument( !contentToMove.equals( contentToMoveBefore ),
                                         "contentToMove and contentToMoveBefore must be different" );
            return new ReorderChildContentParams( this );
        }
    }
}
