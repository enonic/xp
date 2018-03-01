package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

abstract class NodeState
{
    private final String id;

    private final String hostName;

    private final Boolean isMaster;

    private final String version;

    NodeState( Builder builder )
    {
        this.id = builder.id;
        this.hostName = builder.hostName;
        this.isMaster = builder.isMaster;
        this.version = builder.version;
    }

    ObjectNode toJson()
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

        private String version;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        B id( final String id )
        {
            this.id = id;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B hostName( final String hostName )
        {
            this.hostName = hostName;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B master( final Boolean master )
        {
            this.isMaster = master;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B version( final String version )
        {
            this.version = version;
            return (B) this;
        }
    }
}
