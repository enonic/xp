package com.enonic.wem.core.rendering;


public final class ControllerResult
{
    private final boolean success;

    private ControllerResult( final Builder builder )
    {
        this.success = builder.success;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isError()
    {
        return !success;
    }

    public static ControllerResult.Builder newControllerResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Boolean success;

        private Builder()
        {
        }

        public Builder success()
        {
            this.success = true;
            return this;
        }

        public Builder error()
        {
            this.success = false;
            return this;
        }

        public ControllerResult build()
        {
            return new ControllerResult( this );
        }
    }
}
