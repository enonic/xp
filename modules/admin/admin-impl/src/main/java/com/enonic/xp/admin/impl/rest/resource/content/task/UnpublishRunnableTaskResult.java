package com.enonic.xp.admin.impl.rest.resource.content.task;

public class UnpublishRunnableTaskResult
    extends RunnableTaskResult
{

    private UnpublishRunnableTaskResult( Builder builder )
    {
        super( builder );
    }

    @Override
    public String getMessage()
    {
        return new UnpublishTaskMessageGenerator().generate( this );
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

        public UnpublishRunnableTaskResult build()
        {
            return new UnpublishRunnableTaskResult( this );
        }
    }
}
