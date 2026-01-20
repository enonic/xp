package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UpdateContentMetadataResult
{
    private final Content content;

    private UpdateContentMetadataResult( Builder builder )
    {
        this.content = builder.content;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content getContent()
    {
        return content;
    }


    public static final class Builder
    {
        private Content content;

        private Builder()
        {
        }

        public Builder content( Content content )
        {
            this.content = content;
            return this;
        }

        public UpdateContentMetadataResult build()
        {
            return new UpdateContentMetadataResult( this );
        }
    }
}
