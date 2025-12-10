package com.enonic.xp.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

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

    private final List<Action> actions;

    private ContentVersion( Builder builder )
    {
        this.versionId = builder.versionId;
        this.contentId = builder.contentId;
        this.path = builder.path;
        this.timestamp = builder.timestamp;
        this.comment = builder.comment;
        this.actions = builder.actions.build();
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

    public List<Action> getActions()
    {
        return actions;
    }

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final ContentVersion that && Objects.equals( versionId, that.versionId ) &&
            Objects.equals( contentId, that.contentId ) && Objects.equals( path, that.path ) &&
            Objects.equals( timestamp, that.timestamp ) && Objects.equals( comment, that.comment ) &&
            Objects.equals( actions, that.actions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( versionId, contentId, path, timestamp, comment, actions );
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

        private final ImmutableList.Builder<Action> actions = ImmutableList.builder();

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

        public Builder addAction( Action action )
        {
            actions.add( action );
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }

    public record Action(String operation, List<String> fields, PrincipalKey user, Instant opTime)
    {
    }
}
