package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;


public class DeleteRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<ContentPath> deleted;

    private final List<ContentPath> pending;

    private final List<ContentPath> failed;


    private DeleteRunnableTaskResult( Builder builder )
    {
        super( builder );
        this.deleted = builder.deleted;
        this.pending = builder.pending;
        this.failed = builder.failed;
    }


    public List<ContentPath> getPending()
    {
        return pending;
    }

    public List<ContentPath> getFailed()
    {
        return failed;
    }

    public List<ContentPath> getDeleted()
    {
        return deleted;
    }

    @Override
    public String getMessage()
    {
        return new DeleteTaskMessageGenerator().generate( this );
    }

    public int getSuccessCount()
    {
        return pending.size() + deleted.size();
    }

    public int getFailureCount()
    {
        return failed.size();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RunnableTaskResult.Builder
    {
        private List<ContentPath> deleted = Lists.newArrayList();

        private List<ContentPath> pending = Lists.newArrayList();

        private List<ContentPath> failed = Lists.newArrayList();

        private ContentPath destination;

        private Builder()
        {
            super();
        }

        public Builder deleted( ContentPath item )
        {
            this.deleted.add( item );
            return this;
        }

        public Builder pending( ContentPath item )
        {
            this.pending.add( item );
            return this;
        }

        public Builder failed( ContentPath item )
        {
            this.failed.add( item );
            return this;
        }

        public DeleteRunnableTaskResult build()
        {
            return new DeleteRunnableTaskResult( this );
        }
    }
}
