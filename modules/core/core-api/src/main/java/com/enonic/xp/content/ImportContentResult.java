package com.enonic.xp.content;

import com.google.common.base.Preconditions;

public class ImportContentResult
{
    private final Content content;

    private final boolean preExisting;

    private ImportContentResult( Builder builder )
    {
        this.content = builder.content;
        this.preExisting = builder.preExisting;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content getContent()
    {
        return content;
    }

    public boolean isPreExisting()
    {
        return preExisting;
    }

    public static final class Builder
    {
        private Content content;

        private boolean preExisting;

        private Builder()
        {
        }

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        public Builder preExisting( final boolean preExisting )
        {
            this.preExisting = preExisting;
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
