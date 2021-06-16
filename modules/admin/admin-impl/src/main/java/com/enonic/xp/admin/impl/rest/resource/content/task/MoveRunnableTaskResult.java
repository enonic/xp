package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.ContentPath;


public class MoveRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<ContentPath> alreadyMoved;

    private final List<ContentPath> existsFailed;

    private final List<ContentPath> notExistsFailed;

    private final List<ContentPath> accessFailed;

    private final ContentPath destination;

    private MoveRunnableTaskResult( Builder builder )
    {
        super( builder );
        this.alreadyMoved = builder.alreadyMoved;
        this.existsFailed = builder.existsFailed;
        this.notExistsFailed = builder.notExistsFailed;
        this.accessFailed = builder.accessFailed;
        this.destination = builder.destination;
    }

    public List<ContentPath> getAlreadyMoved()
    {
        return alreadyMoved;
    }

    public List<ContentPath> getExistsFailed()
    {
        return existsFailed;
    }

    public List<ContentPath> getNotExistsFailed()
    {
        return notExistsFailed;
    }

    public List<ContentPath> getAccessFailed()
    {
        return accessFailed;
    }

    public ContentPath getDestination()
    {
        return destination;
    }

    @Override
    public int getSuccessCount()
    {
        return super.getSuccessCount() + alreadyMoved.size();
    }

    @Override
    public int getFailureCount()
    {
        return super.getFailureCount() + existsFailed.size() + notExistsFailed.size() + accessFailed.size();
    }

    @Override
    public String getMessage()
    {
        return new MoveTaskMessageGenerator().generate( this );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RunnableTaskResult.Builder<Builder>
    {
        private final List<ContentPath> alreadyMoved = new ArrayList<>();

        private final List<ContentPath> existsFailed = new ArrayList<>();

        private final List<ContentPath> notExistsFailed = new ArrayList<>();

        private final List<ContentPath> accessFailed = new ArrayList<>();

        private ContentPath destination;

        private Builder()
        {
            super();
        }

        public Builder destination( ContentPath destination )
        {
            this.destination = destination;
            return this;
        }

        public Builder succeeded( String item )
        {
            super.succeeded( ContentPath.from( item ) );
            return this;
        }

        public Builder alreadyMoved( ContentPath item )
        {
            this.alreadyMoved.add( item );
            return this;
        }

        public Builder existsFailed( ContentPath item )
        {
            this.existsFailed.add( item );
            return this;
        }

        public Builder notExistsFailed( ContentPath item )
        {
            this.notExistsFailed.add( item );
            return this;
        }

        public Builder accessFailed( ContentPath item )
        {
            this.accessFailed.add( item );
            return this;
        }

        @Override
        public MoveRunnableTaskResult build()
        {
            return new MoveRunnableTaskResult( this );
        }
    }
}
