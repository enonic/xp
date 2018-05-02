package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;


public class DeleteRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<ContentPath> pending;


    private DeleteRunnableTaskResult( Builder builder )
    {
        super( builder );
        this.pending = builder.pending;
    }


    public List<ContentPath> getPending()
    {
        return pending;
    }

    @Override
    public String getMessage()
    {
        return new DeleteTaskMessageGenerator().generate( this );
    }

    public int getSuccessCount()
    {
        return pending.size() + super.getSuccessCount();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RunnableTaskResult.Builder<Builder>
    {
        private List<ContentPath> pending = Lists.newArrayList();

        private Builder()
        {
            super();
        }

        public Builder pending( ContentPath item )
        {
            this.pending.add( item );
            return this;
        }

        public DeleteRunnableTaskResult build()
        {
            return new DeleteRunnableTaskResult( this );
        }

        public Builder pending( final ContentIds items )
        {
            this.pending.addAll( items.stream().map( i -> ContentPath.from( i.toString() ) ).collect( Collectors.toList() ) );
            return this;
        }
    }
}
