package com.enonic.xp.elasticsearch.impl.status;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class NodeState
{
    protected String id;

    protected String hostName;

    protected Boolean isMaster;

    protected NodeState( Builder builder )
    {
        this.id = builder.id;
        this.hostName = builder.hostName;
        this.isMaster = builder.isMaster;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "hostName", this.hostName );
        json.put( "id", this.id );
        json.put( "isMaster", this.isMaster );
        return json;
    }


    public abstract static class Builder
    {

        private String id;

        private String hostName;

        private Boolean isMaster;

        protected Builder()
        {
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder hostName( final String hostName )
        {
            this.hostName = hostName;
            return this;
        }

        public Builder master( final Boolean master )
        {
            this.isMaster = master;
            return this;
        }

        public abstract NodeState build();

    }
}
