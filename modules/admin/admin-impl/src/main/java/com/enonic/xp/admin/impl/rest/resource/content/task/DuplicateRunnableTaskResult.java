package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.ContentPath;

public class DuplicateRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<ContentPath> alreadyDuplicated;

    private DuplicateRunnableTaskResult( Builder builder )
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

    public static class Builder
        extends RunnableTaskResult.Builder<Builder>
    {
        private List<ContentPath> alreadyDuplicated = new ArrayList<>();

        private Builder()
        {
            super();
        }

        @Override
        public Builder succeeded( ContentPath item )
        {
            super.succeeded( item );
            return this;
        }

        public Builder alreadyDuplicated( ContentPath item )
        {
            this.alreadyDuplicated.add( item );
            return this;
        }

        @Override
        public DuplicateRunnableTaskResult build()
        {
            return new DuplicateRunnableTaskResult( this );
        }
    }
}
