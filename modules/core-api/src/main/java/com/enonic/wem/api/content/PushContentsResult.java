package com.enonic.wem.api.content;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PushContentsResult
{
    private final Contents successfull;

    private final ImmutableSet<Failed> failed;

    public PushContentsResult( final Contents successfull, final ImmutableSet<Failed> failed )
    {
        this.successfull = successfull;
        this.failed = failed;
    }

    private PushContentsResult( Builder builder )
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

        private final Reason reason;

        public Failed( final Content content, final Reason reason )
        {
            this.content = content;
            this.reason = reason;
        }

        public Content getContent()
        {
            return content;
        }

        public Reason getReason()
        {
            return reason;
        }
    }

    public enum Reason
    {
        PARENT_NOT_EXISTS( "Parent content does not exist" ),
        UNKNOWN( "Unknown" );

        private final String message;

        Reason( final String message )
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

        public Builder addFailed( final Content content, final Reason reason )
        {
            this.failed.add( new Failed( content, reason ) );
            return this;
        }

        public PushContentsResult build()
        {
            return new PushContentsResult( this );
        }
    }
}
