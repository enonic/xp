package com.enonic.wem.api.content;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PushContentsResult
{
    private final Contents successfull;

    private final ImmutableSet<Failed> failed;

    private PushContentsResult( final Builder builder )
    {
        successfull = builder.successfull;
        failed = ImmutableSet.copyOf( builder.failed );
    }

    public Contents getSuccessfull()
    {
        return successfull;
    }

    public ImmutableSet<Failed> getFailed()
    {
        return failed;
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

    public static class ContentToPush
    {
        private final ContentId contentId;

        private String description;

        private ContentToPush( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        private ContentToPush( final ContentId contentId, final String description )
        {
            this.description = description;
            this.contentId = contentId;
        }

        public ContentId getContentId()
        {
            return contentId;
        }

        public String getDescription()
        {
            return description;
        }
    }


    public static final class Builder
    {
        private Contents successfull;

        private final Set<Failed> failed = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder successfull( Contents successfull )
        {
            this.successfull = successfull;
            return this;
        }

        public Builder addFailed( final Content content, final FailedReason failedReason )
        {
            this.failed.add( new Failed( content, failedReason ) );
            return this;
        }

        public PushContentsResult build()
        {
            return new PushContentsResult( this );
        }
    }
}
