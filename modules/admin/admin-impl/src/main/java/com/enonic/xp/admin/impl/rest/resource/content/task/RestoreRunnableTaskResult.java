package com.enonic.xp.admin.impl.rest.resource.content.task;

public class RestoreRunnableTaskResult
    extends RunnableTaskResult
{

    private RestoreRunnableTaskResult( Builder builder )
    {
        super( builder );
    }

    @Override
    public String getMessage()
    {
        return new RestoreTaskMessageGenerator().generate( this );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RunnableTaskResult.Builder<Builder>
    {
        private Builder()
        {
            super();
        }

        @Override
        public RestoreRunnableTaskResult build()
        {
            return new RestoreRunnableTaskResult( this );
        }
    }
}
