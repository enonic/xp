package com.enonic.xp.content;

import com.google.common.base.Preconditions;

public final class ImportContentResult
{
    private final Content content;

    private ImportContentResult( Builder builder )
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

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( content, "content cannot be null" );
        }

        public ImportContentResult build()
        {
            this.validate();
            return new ImportContentResult( this );
        }
    }
}
