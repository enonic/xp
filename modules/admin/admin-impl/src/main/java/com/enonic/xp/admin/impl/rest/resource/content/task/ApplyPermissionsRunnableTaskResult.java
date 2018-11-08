package com.enonic.xp.admin.impl.rest.resource.content.task;

public class ApplyPermissionsRunnableTaskResult
    extends RunnableTaskResult
{
    private ApplyPermissionsRunnableTaskResult( Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String getMessage()
    {
        return new ApplyPermissionsTaskMessageGenerator().generate( this );
    }

    public static class Builder
        extends RunnableTaskResult.Builder<Builder>
    {
        public ApplyPermissionsRunnableTaskResult build()
        {
            return new ApplyPermissionsRunnableTaskResult( this );
        }
    }
}
