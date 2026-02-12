package com.enonic.xp.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNull;

@PublicApi
@NullMarked
public record ContentVersion(ContentVersionId versionId, ContentId contentId, ContentPath path, Instant timestamp, @Nullable String comment,
                             List<Action> actions)
{
    public ContentVersion
    {
        requireNonNull( versionId );
        requireNonNull( contentId );
        requireNonNull( path );
        requireNonNull( timestamp );
        actions = List.copyOf( actions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        @Nullable
        private ContentPath path;

        @Nullable
        private Instant timestamp;

        @Nullable
        private String comment;

        @Nullable
        private ContentVersionId versionId;

        @Nullable
        private ContentId contentId;

        private final List<Action> actions = new ArrayList<>();

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

        public Builder comment( final @Nullable String comment )
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
            return new ContentVersion( requireNonNull( this.versionId ), requireNonNull( this.contentId ), requireNonNull( this.path ),
                                       requireNonNull( this.timestamp ), this.comment, this.actions );
        }
    }

    public record Action(String operation, List<String> fields, PrincipalKey user, Instant opTime)
    {
        public Action
        {
            requireNonNull( operation );
            fields = List.copyOf( fields );
            requireNonNull( user );
            requireNonNull( opTime );
        }
    }
}
