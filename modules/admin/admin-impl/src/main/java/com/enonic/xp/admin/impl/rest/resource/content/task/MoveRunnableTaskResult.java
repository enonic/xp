package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;


public class MoveRunnableTaskResult
    extends RunnableTaskResult
{
    private final List<String> moved;

    private final List<ContentPath> alreadyMoved;

    private final List<ContentPath> existsFailed;

    private final List<ContentPath> notExistsFailed;

    private final List<ContentPath> accessFailed;

    private final List<ContentPath> failed;

    private final ContentPath destination;

    private MoveRunnableTaskResult( Builder builder )
    {
        super( builder );
        this.moved = builder.moved;
        this.alreadyMoved = builder.alreadyMoved;
        this.existsFailed = builder.existsFailed;
        this.notExistsFailed = builder.notExistsFailed;
        this.accessFailed = builder.accessFailed;
        this.failed = builder.failed;
        this.destination = builder.destination;
    }

    public List<String> getMoved()
    {
        return moved;
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

    public List<ContentPath> getFailed()
    {
        return failed;
    }

    public ContentPath getDestination()
    {
        return destination;
    }

    public int getSuccessCount()
    {
        return moved.size() + alreadyMoved.size();
    }

    public int getFailureCount()
    {
        return existsFailed.size() + notExistsFailed.size() + accessFailed.size() + failed.size();
    }

    public String toJson()
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode map = mapper.createObjectNode();
        final String message = new MoveTaskMessageGenerator().generate( this );

        map.put( "state", getState().toString() );
        map.put( "message", message );

        return map.toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RunnableTaskResult.Builder
    {

        private List<String> moved = Lists.newArrayList();

        private List<ContentPath> alreadyMoved = Lists.newArrayList();

        private List<ContentPath> existsFailed = Lists.newArrayList();

        private List<ContentPath> notExistsFailed = Lists.newArrayList();

        private List<ContentPath> accessFailed = Lists.newArrayList();

        private List<ContentPath> failed = Lists.newArrayList();

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

        public Builder moved( String item )
        {
            this.moved.add( item );
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

        public Builder failed( ContentPath item )
        {
            this.failed.add( item );
            return this;
        }

        public MoveRunnableTaskResult build()
        {
            return new MoveRunnableTaskResult( this );
        }
    }
}
