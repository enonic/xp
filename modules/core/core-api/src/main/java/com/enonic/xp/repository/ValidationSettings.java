package com.enonic.xp.repository;

public class ValidationSettings
{
    private boolean checkParentExists;

    private boolean checkExists;

    private ValidationSettings( final Builder builder )
    {
        checkParentExists = builder.checkParentExists;
        checkExists = builder.checkExists;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private boolean checkParentExists = true;

        private boolean checkExists = true;

        private Builder()
        {
        }

        public Builder checkParentExists( final boolean checkParentExists )
        {
            this.checkParentExists = checkParentExists;
            return this;
        }

        public Builder checkExists( final boolean checkExists )
        {
            this.checkExists = checkExists;
            return this;
        }

        public ValidationSettings build()
        {
            return new ValidationSettings( this );
        }
    }
}
