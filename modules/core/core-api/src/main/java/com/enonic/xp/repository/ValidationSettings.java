package com.enonic.xp.repository;

public class ValidationSettings
{
    private final boolean checkParentExists;

    private final boolean checkExists;

    private final boolean checkPermissions;

    private ValidationSettings( final Builder builder )
    {
        checkParentExists = builder.checkParentExists;
        checkExists = builder.checkExists;
        checkPermissions = builder.checkPermissions;
    }

    public boolean isCheckExists()
    {
        return checkExists;
    }

    public boolean isCheckParentExists()
    {
        return checkParentExists;
    }

    public boolean isCheckPermissions()
    {
        return checkPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private boolean checkParentExists = true;

        private boolean checkExists = true;

        private boolean checkPermissions = true;

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

        public Builder checkPermissions( final boolean checkExists )
        {
            this.checkPermissions = checkPermissions;
            return this;
        }

        public ValidationSettings build()
        {
            return new ValidationSettings( this );
        }
    }
}
