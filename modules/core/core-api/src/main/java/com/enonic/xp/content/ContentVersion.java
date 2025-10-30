package com.enonic.xp.content;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class ContentVersion
{
    private final ContentVersionId versionId;

    private final ContentId contentId;

    private final ContentPath path;

    private final Instant timestamp;

    private final String comment;

    private final List<Change> changes;

    private ContentVersion( Builder builder )
    {
        this.versionId = builder.versionId;
        this.contentId = builder.contentId;
        this.path = builder.path;
        this.timestamp = builder.timestamp;
        this.comment = builder.comment;
        this.changes = builder.changes.build();
    }

    public ContentVersionId getVersionId()
    {
        return versionId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getComment()
    {
        return comment;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public List<Change> getChanges()
    {
        return changes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentPath path;

        private Instant timestamp;

        private String comment;

        private ContentVersionId versionId;

        private ContentId contentId;

        private final ImmutableList.Builder<Change> changes = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder versionId( final ContentVersionId id )
        {
            this.versionId = id;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder comment( final String comment )
        {
            this.comment = comment;
            return this;
        }

        public Builder addChange( Change change )
        {
            changes.add( change );
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }

    public record Change(String operation, List<String> fields, PrincipalKey user, Instant opTime)
    {
    }
}
