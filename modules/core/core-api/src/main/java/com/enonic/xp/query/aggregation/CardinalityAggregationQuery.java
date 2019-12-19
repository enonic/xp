package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public class CardinalityAggregationQuery
    extends MetricAggregationQuery
{
    private final String fieldName;


    private CardinalityAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
//        this.size = builder.size;
//        this.orderDirection = builder.direction;
//        this.orderType = builder.type;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

//    public int getSize()
//    {
//        return size;
//    }
//
//    public Direction getOrderDirection()
//    {
//        return orderDirection;
//    }
//
//    public Type getOrderType()
//    {
//        return orderType;
//    }

    public String getFieldName()
    {
        return fieldName;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", fieldName ).
//            add( "size", size ).
//            add( "orderDirection", orderDirection ).
//            add( "orderType", orderType ).
    toString();
    }

    public static class Builder
        extends MetricAggregationQuery.Builder<Builder>
    {
//        private Direction direction = Direction.ASC;
//
//        private Type type = Type.TERM;

        private String fieldName;

        public Builder( final String name )
        {
            super( name );
        }

//        private int size = TERM_DEFAULT_SIZE;

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

//        public Builder size( final Integer size )
//        {
//            this.size = size;
//            return this;
//        }


        public CardinalityAggregationQuery build()
        {
            return new CardinalityAggregationQuery( this );
        }

//        public Builder orderDirection( final Direction direction )
//        {
//            this.direction = direction;
//            return this;
//        }
//
//        public Builder orderType( final Type type )
//        {
//            this.type = type;
//            return this;
//        }
    }

//    public enum Direction
//    {
//        ASC, DESC
//    }
//
//    public enum Type
//    {
//        TERM, DOC_COUNT
//    }


}
