package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;

public class DuplicateRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<ContentPath> alreadyDuplicated;

    protected DuplicateRunnableTaskResult( Builder builder )
    {
        super( builder );
        this.alreadyDuplicated = builder.alreadyDuplicated;
    }

    public List<ContentPath> getAlreadyDuplicated()
    {
        return alreadyDuplicated;
    }

    @Override
    public int getSuccessCount()
    {
        return super.getSuccessCount() + alreadyDuplicated.size();
    }

    @Override
    public String getMessage()
    {
        return new DuplicateTaskMessageGenerator().generate( this );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder<BUILDER extends Builder>
        extends RunnableTaskResult.Builder<BUILDER>
    {
        private List<ContentPath> alreadyDuplicated = Lists.newArrayList();

        protected Builder()
        {
            super();
        }

        public BUILDER succeeded( ContentPath item )
        {
            super.succeeded( item );
            return (BUILDER) this;
        }

        public BUILDER alreadyDuplicated( ContentPath item )
        {
            this.alreadyDuplicated.add( item );
            return (BUILDER) this;
        }

        public DuplicateRunnableTaskResult build()
        {
            return new DuplicateRunnableTaskResult( this );
        }
    }
}
