package com.enonic.xp.admin.impl.rest.resource.content.task;

public class ArchiveRunnableTaskResult
    extends RunnableTaskResult
{

    private ArchiveRunnableTaskResult( Builder builder )
    {
        super( builder );
    }

    @Override
    public String getMessage()
    {
        return new ArchiveTaskMessageGenerator().generate( this );
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
        public ArchiveRunnableTaskResult build()
        {
            return new ArchiveRunnableTaskResult( this );
        }
    }
}
