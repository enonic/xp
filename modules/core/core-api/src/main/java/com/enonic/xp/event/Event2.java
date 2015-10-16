package com.enonic.xp.event;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.convert.Converters;

public final class Event2
    implements Event
{

    private String type;

    private long timestamp;

    private boolean distributed;

    private Map<String, ?> data;

    private Event2( Builder builder )
    {
        this.type = builder.type;
        this.timestamp = Instant.now().toEpochMilli();
        this.distributed = builder.distributed;
        this.data = builder.dataBuilder.build();
    }

    public String getType()
    {
        return this.type;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public boolean isDistributed()
    {
        return this.distributed;
    }

    public Map<String, ?> getData()
    {
        return this.data;
    }

    public Optional<Object> getValue( String key )
    {
        return Optional.ofNullable( data.get( key ) );
    }

    public boolean hasValue( String key )
    {
        return this.data.containsKey( key );
    }

    public <T> Optional<T> getValueAs( Class<T> type, String key )
    {
        Optional<Object> value = this.getValue( key );
        if ( value.isPresent() )
        {
            return Optional.of( Converters.convert( this.getValue( key ).get(), type ) );
        }
        return Optional.empty();
    }

    public JsonNode toJson()
    {

        final ObjectNode dataJsonNode = newObjectNode();
        for ( final Map.Entry<String, ?> entry : this.data.entrySet() )
        {
            final String key = entry.getKey();
            dataJsonNode.put( key, entry.getValue().toString() );
        }

        final ObjectNode json = newObjectNode();
        json.put( "type", this.type );
        json.put( "timestamp", this.timestamp );
        json.put( "distributed", this.distributed );
        json.set( "data", dataJsonNode );
        return json;
    }

    private ObjectNode newObjectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    public boolean isType( String type )
    {
        return type.equals( this.type );
    }

    public boolean isSubType( String type )
    {
        return this.type.startsWith( type ) && !this.type.equals( type );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", this.type ).
            add( "timestamp", this.timestamp ).
            add( "distributed", this.distributed ).
            add( "data", this.data != null ? this.data.toString() : null ).
            omitNullValues().
            toString();
    }

    public static Builder create( String type )
    {
        return new Builder( type );
    }

    public static Builder create( Event2 event )
    {
        return new Builder( event );
    }

    static class Builder
    {

        private String type;

        private boolean distributed;

        private ImmutableMap.Builder<String, Object> dataBuilder = ImmutableMap.builder();

        private Builder( String type )
        {
            this.type = type;
        }

        private Builder( Event2 event )
        {
            this.type = event.type;
            this.distributed = event.distributed;

            for ( final Map.Entry<String, ?> entry : event.data.entrySet() )
            {
                final String key = entry.getKey();
                this.value( key, entry.getValue() );
            }
        }

        public Builder distributed( boolean flag )
        {
            this.distributed = flag;
            return this;
        }

        public Builder value( String key, Object value )
        {
            if ( value != null )
            {
                if ( value instanceof Number || value instanceof Boolean )
                {
                    dataBuilder.put( key, value );
                }
                else
                {
                    dataBuilder.put( key, value.toString() );
                }
            }
            return this;
        }

        public Event2 build()
        {
            return new Event2( this );
        }
    }

}
