package com.enonic.xp.admin.impl.rest.resource.content.json;

public class PublishContentResultJson
{
    private final Integer successes;

    private final Integer failures;

    private final Integer deleted;

    private final String contentName;

    @SuppressWarnings("unused")
    public Integer getSuccesses()
    {
        return successes;
    }

    @SuppressWarnings("unused")
    public Integer getFailures()
    {
        return failures;
    }

    @SuppressWarnings("unused")
    public Integer getDeleted()
    {
        return deleted;
    }

    @SuppressWarnings("unused")
    public String getContentName()
    {
        return contentName;
    }

    @SuppressWarnings("unused")

    private PublishContentResultJson( final Builder builder )
    {
        this.successes = builder.successes;
        this.deleted = builder.deleted;
        this.failures = builder.failures;
        this.contentName = builder.contentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Integer successes;

        private Integer failures;

        private Integer deleted;

        private String contentName;


        public Builder successSize( final Integer contentSize )
        {
            this.successes = contentSize;
            return this;
        }

        public Builder deletedSize( final Integer contentSize )
        {
            this.deleted = contentSize;
            return this;
        }

        public Builder failuresSize( final Integer contentSize )
        {
            this.failures = contentSize;
            return this;
        }

        public Builder contentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public PublishContentResultJson build()
        {
            return new PublishContentResultJson( this );
        }

    }
}