package com.enonic.xp.content;

import com.google.common.base.Preconditions;

/**
 * Parameters for {@link ContentService#list(ListContentsByParentParams)}. The parent is given by path or by id, not both.
 * <p>
 * A {@link ContentPath} is relative to the content root of the calling context, which the same API serves the archive through: the very
 * same path names a different content depending on whether the context roots it at the content tree or at the archive. A parent given by
 * id is only listed when it lives below the content root of the context, so listing never reaches from one into the other.
 *
 * @since 8.1.0
 */
public final class ListContentsByParentParams
{
    private final ContentPath parentPath;

    private final ContentId parentId;

    private final boolean recursive;

    private ListContentsByParentParams( final Builder builder )
    {
        Preconditions.checkArgument( builder.parentPath != null ^ builder.parentId != null,
                                     "expected either parentPath or parentId, but not both" );
        this.parentPath = builder.parentPath;
        this.parentId = builder.parentId;
        this.recursive = builder.recursive;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public ContentId getParentId()
    {
        return parentId;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public static final class Builder
    {
        private ContentPath parentPath;

        private ContentId parentId;

        private boolean recursive = false;

        private Builder()
        {
        }

        public Builder parentPath( final ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder parentId( final ContentId parentId )
        {
            this.parentId = parentId;
            return this;
        }

        /**
         * Lists every descendant of the parent instead of the direct children only. Entries come back ordered by path either way.
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        public ListContentsByParentParams build()
        {
            return new ListContentsByParentParams( this );
        }
    }
}
