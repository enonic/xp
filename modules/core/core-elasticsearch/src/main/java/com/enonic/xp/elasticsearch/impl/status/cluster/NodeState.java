package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class NodeState
{
    protected final String id;

    protected final String hostName;

    protected final Boolean isMaster;

    protected final String version;

    protected NodeState( Builder builder )
    {
        this.id = builder.id;
        this.hostName = builder.hostName;
        this.isMaster = builder.isMaster;
        this.version = builder.version;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "isMaster", this.isMaster );
        json.put( "id", this.id );
        json.put( "hostName", this.hostName );
        json.put( "version", this.version );
        return json;
    }


    public abstract static class Builder<B extends Builder>
    {
        private String id;

        private String hostName;

        private Boolean isMaster;

        protected String version;

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B id( final String id )
        {
            this.id = id;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B hostName( final String hostName )
        {
            this.hostName = hostName;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B master( final Boolean master )
        {
            this.isMaster = master;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B version( final String version )
        {
            this.version = version;
            return (B) this;
        }

        public abstract NodeState build();

    }
}
