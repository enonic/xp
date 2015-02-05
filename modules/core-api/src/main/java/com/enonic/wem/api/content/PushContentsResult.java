package com.enonic.wem.api.content;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PushContentsResult
{
    private final Contents pushedContent;

    private final ImmutableSet<Failed> failed;

    private final PushContentRequests pushContentRequests;

    private final ContentIds deleted;

    private PushContentsResult( final Builder builder )
    {
        this.pushedContent = builder.pushedContent;
        this.failed = ImmutableSet.copyOf( builder.failed );
        this.pushContentRequests = builder.pushContentRequests;
        this.deleted = ContentIds.from( builder.deleted );
    }

    public PushContentRequests getPushContentRequests()
    {
        return pushContentRequests;
    }

    public Contents getPushedContent()
    {
        return pushedContent;
    }

    public ImmutableSet<Failed> getFailed()
    {
        return failed;
    }


    public ContentIds getDeleted()
    {
        return deleted;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Failed
    {
        private final Content content;

        private final FailedReason failedReason;

        public Failed( final Content content, final FailedReason failedReason )
        {
            this.content = content;
            this.failedReason = failedReason;
        }

        public Content getContent()
        {
            return content;
        }

        public FailedReason getFailedReason()
        {
            return failedReason;
        }
    }

    public enum FailedReason
    {
        CONTENT_NOT_VALID( "Content not valid" ),
        PARENT_NOT_EXISTS( "Parent content does not exist" ),
        UNKNOWN( "Unknown" );

        private final String message;

        FailedReason( final String message )
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }

    public static final class Builder
    {
        private Contents pushedContent = Contents.empty();

        private final Set<Failed> failed = Sets.newHashSet();

        private final Set<ContentId> deleted = Sets.newHashSet();

        private PushContentRequests pushContentRequests;

        private Builder()
        {
        }

        public Builder setPushedContent( final Contents pushedContent )
        {
            this.pushedContent = pushedContent;
            return this;
        }

        public Builder addDeleted( final ContentId contentId )
        {
            this.deleted.add( contentId );
            return this;
        }

        public Builder addFailed( final Content content, final FailedReason failedReason )
        {
            this.failed.add( new Failed( content, failedReason ) );
            return this;
        }

        public Builder pushContentRequests( final PushContentRequests pushContentRequests )
        {
            this.pushContentRequests = pushContentRequests;
            return this;
        }

        public PushContentsResult build()
        {
            return new PushContentsResult( this );
        }
    }

}
