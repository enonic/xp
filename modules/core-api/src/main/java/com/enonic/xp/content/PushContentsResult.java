package com.enonic.xp.content;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Beta
public class PushContentsResult
{
    private final Contents pushedContent;

    private final Contents childrenPushedContent;

    private final ImmutableSet<Failed> failed;

    private final PushContentRequests pushContentRequests;

    private final Contents deleted;

    private PushContentsResult( final Builder builder )
    {
        this.pushedContent = Contents.from( builder.pushedContent );
        this.childrenPushedContent = Contents.from( builder.childrenPushedContent );
        this.failed = ImmutableSet.copyOf( builder.failed );
        this.pushContentRequests = builder.pushContentRequests;
        this.deleted = Contents.from( builder.deleted );
    }

    public PushContentRequests getPushContentRequests()
    {
        return pushContentRequests;
    }

    public Contents getPushedContent()
    {
        return pushedContent;
    }

    public Contents getChildrenPushedContent()
    {
        return childrenPushedContent;
    }

    public ImmutableSet<Failed> getFailed()
    {
        return failed;
    }

    public Contents getDeleted()
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
        ACCESS_DENIED( "Not enough permissions to publish content" ),
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

        private final Set<Content> pushedContent = Sets.newHashSet();

        private final Set<Content> childrenPushedContent = Sets.newHashSet();

        private final Set<Failed> failed = Sets.newHashSet();

        private final Set<Content> deleted = Sets.newHashSet();

        private PushContentRequests pushContentRequests;

        private Builder()
        {
        }

        public Builder addPushedContent( final Contents pushedContent )
        {
            this.pushedContent.addAll( pushedContent.getSet() );
            return this;
        }

        public Builder addChildrenPushedContent( final Contents childrenPushedContent )
        {
            this.childrenPushedContent.addAll( childrenPushedContent.getSet() );
            return this;
        }

        public Builder addDeleted( final Contents contents )
        {
            this.deleted.addAll( contents.getSet() );
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