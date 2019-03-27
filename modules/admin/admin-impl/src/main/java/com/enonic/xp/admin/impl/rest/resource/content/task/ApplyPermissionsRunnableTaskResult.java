package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.content.ContentPaths;

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
        public Builder succeeded( ContentPaths items )
        {
            this.succeeded.addAll( items.getSet() );
            return this;
        }

        public Builder failed( ContentPaths items )
        {
            this.failed.addAll( items.getSet() );
            return this;
        }

        public ApplyPermissionsRunnableTaskResult build()
        {
            return new ApplyPermissionsRunnableTaskResult( this );
        }
    }
}
