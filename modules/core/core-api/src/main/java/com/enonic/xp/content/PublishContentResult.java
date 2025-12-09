package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PublishContentResult
{
    private final ImmutableList<Result> publishedContents;

    private final ImmutableList<Result> failedContents;

    private PublishContentResult( Builder builder )
    {
        this.publishedContents = builder.publishedContents.build();
        this.failedContents = builder.failedContents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPushedContents()
    {
        return publishedContents.stream().map( Result::contentId ).collect( ContentIds.collector() );
    }

    public ContentIds getFailedContents()
    {
        return failedContents.stream().map( Result::contentId ).collect( ContentIds.collector() );
    }

    public List<Result> getFailed()
    {
        return failedContents;
    }

    public List<Result> getPublished()
    {
        return publishedContents;
    }

    public static final class Builder
    {
        private ImmutableList.Builder<Result> publishedContents = ImmutableList.builder();

        private ImmutableList.Builder<Result> failedContents = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( Result result )
        {
            if ( result.failureReason == null )
            {
                publishedContents.add( result );
            }
            else
            {
                failedContents.add( result );
            }
            return this;
        }

        public PublishContentResult build()
        {
            return new PublishContentResult( this );
        }
    }

    public enum Reason
    {
        ALREADY_EXIST, PARENT_NOT_FOUND, ACCESS_DENIED, INVALID, NOT_READY
    }

    public record Result(ContentId contentId, Reason failureReason)
    {
        public static Result success( final ContentId contentId )
        {
            return new Result( contentId, null );
        }

        public static Result failure( final ContentId contentId, Reason failureReason )
        {
            return new Result( contentId, Objects.requireNonNull( failureReason ) );
        }
    }
}
