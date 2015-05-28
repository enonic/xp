package com.enonic.xp.web.servlet;

public class UriRewritingResult
{
    private final String rewrittenUri;

    private final String deletedUriPrefix;

    private final String newUriPrefix;

    public String getRewrittenUri()
    {
        return rewrittenUri;
    }

    public String getDeletedUriPrefix()
    {
        return deletedUriPrefix;
    }

    public String getNewUriPrefix()
    {
        return newUriPrefix;
    }

    private UriRewritingResult( final Builder builder )
    {
        this.rewrittenUri = builder.rewrittenUri;
        this.deletedUriPrefix = builder.deletedUriPrefix;
        this.newUriPrefix = builder.newUriPrefix;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String rewrittenUri;

        private String deletedUriPrefix;

        private String newUriPrefix;

        private Builder()
        {
        }

        public Builder rewrittenUri( String rewrittenUri )
        {
            this.rewrittenUri = rewrittenUri;
            return this;
        }

        public Builder deletedUriPrefix( String deletedUriPrefix )
        {
            this.deletedUriPrefix = deletedUriPrefix;
            return this;
        }

        public Builder newUriPrefix( String newUriPrefix )
        {
            this.newUriPrefix = newUriPrefix;
            return this;
        }

        public UriRewritingResult build()
        {
            return new UriRewritingResult( this );
        }

    }
}
