package com.enonic.xp.event;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.convert.Converters;

public final class Event
{
    private final String type;

    private final long timestamp;

    private final boolean distributed;

    private final boolean localOrigin;

    private final ImmutableMap<String, Object> data;

    private Event( final Builder builder )
    {
        this.type = builder.type;
        this.timestamp = builder.timestamp;
        this.distributed = builder.distributed;
        this.data = builder.dataBuilder.build();
        this.localOrigin = builder.localOrigin;
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

    public Map<String, Object> getData()
    {
        return this.data;
    }

    public Optional<Object> getValue( final String key )
    {
        return Optional.ofNullable( data.get( key ) );
    }

    public boolean hasValue( final String key )
    {
        return this.data.containsKey( key );
    }

    public <T> Optional<T> getValueAs( final Class<T> type, final String key )
    {
        Optional<Object> value = this.getValue( key );
        if ( value.isPresent() )
        {
            return Optional.of( Converters.convert( this.getValue( key ).get(), type ) );
        }
        return Optional.empty();
    }

    public boolean isType( final String type )
    {
        return type.equals( this.type );
    }

    public boolean isSubType( final String type )
    {
        return this.type.startsWith( type + "." );
    }

    public boolean isLocalOrigin()
    {
        return localOrigin;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", this.type ).
            add( "timestamp", this.timestamp ).
            add( "distributed", this.distributed ).
            add( "localOrigin", this.localOrigin ).
            add( "data", this.data != null ? this.data.toString() : null ).
            omitNullValues().
            toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Event event = (Event) o;

        if ( timestamp != event.timestamp )
        {
            return false;
        }
        if ( distributed != event.distributed )
        {
            return false;
        }
        if ( !type.equals( event.type ) )
        {
            return false;
        }
        return data.equals( event.data );
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + (int) ( timestamp ^ ( timestamp >>> 32 ) );
        result = 31 * result + ( distributed ? 1 : 0 );
        result = 31 * result + data.hashCode();
        return result;
    }

    public static Builder create( final String type )
    {
        return new Builder( type );
    }

    public static Builder create( final Event event )
    {
        return new Builder( event );
    }

    public static final class Builder
    {
        private String type;

        private boolean distributed;

        private long timestamp;

        private boolean localOrigin = true;

        private ImmutableMap.Builder<String, Object> dataBuilder = ImmutableMap.builder();

        private Builder( final String type )
        {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

        private Builder( final Event event )
        {
            this( event.type );
            this.distributed = event.distributed;
            this.timestamp = event.timestamp;
            this.localOrigin = event.localOrigin;

            for ( final Map.Entry<String, ?> entry : event.data.entrySet() )
            {
                final String key = entry.getKey();
                this.value( key, entry.getValue() );
            }
        }

        public Builder distributed( final boolean flag )
        {
            this.distributed = flag;
            return this;
        }

        public Builder value( final String key, final Object value )
        {
            if ( value == null )
            {
                return this;
            }

            if ( value instanceof Number || value instanceof Boolean || value instanceof Collection || value instanceof Map )
            {
                dataBuilder.put( key, value );
                return this;
            }

            dataBuilder.put( key, value.toString() );
            return this;
        }

        public Builder timestamp( final long timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder localOrigin( final boolean local )
        {
            this.localOrigin = local;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( type, "type cannot be null" );
        }


        public Event build()
        {
            this.validate();
            return new Event( this );
        }
    }
}
