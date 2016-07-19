package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class DeleteContentsResult
{
    private final ContentIds deletedContents;

    private final ContentIds pendingContents;

    private final String contentName;

    private final String contentType;

    private DeleteContentsResult( Builder builder )
    {
        this.pendingContents = ContentIds.from( builder.pendingContents );
        this.deletedContents = ContentIds.from( builder.deletedContents );
        this.contentName = builder.contentName;
        this.contentType = builder.contentType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPendingContents()
    {
        return pendingContents;
    }

    public ContentIds getDeletedContents()
    {
        return deletedContents;
    }

    public String getContentName()
    {
        return contentName;
    }

    public String getContentType()
    {
        return contentType;
    }

    public static final class Builder
    {
        private List<ContentId> pendingContents = Lists.newArrayList();

        private List<ContentId> deletedContents = Lists.newArrayList();

        private String contentName;

        private String contentType;

        private Builder()
        {
        }

        public Builder addPending( final ContentId pendingContent )
        {
            this.pendingContents.add( pendingContent );
            return this;
        }

        public Builder addPending( final ContentIds pendingContents )
        {
            this.pendingContents.addAll( pendingContents.getSet() );
            return this;
        }

        public Builder addDeleted( final ContentId deletedContent )
        {
            this.deletedContents.add( deletedContent );
            return this;
        }

        public Builder addDeleted( final ContentIds deletedContents )
        {
            this.deletedContents.addAll( deletedContents.getSet() );
            return this;
        }


        public Builder setContentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public Builder setContentType( final String contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public DeleteContentsResult build()
        {
            return new DeleteContentsResult( this );
        }
    }
}