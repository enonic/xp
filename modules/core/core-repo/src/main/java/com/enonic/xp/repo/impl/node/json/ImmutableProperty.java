package com.enonic.xp.repo.impl.node.json;

import java.util.List;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueType;

public abstract class ImmutableProperty
{
    /**
     * ValueSet with null is a special case when PropertySet is null.
     */
    private static final ValueSet NULL_VALUE_SET = new ValueSetN();

    private static final ValueSet EMPTY_VALUE_SET = new ValueSetN( List.of() );

    final String name;

    private ImmutableProperty( final String name )
    {
        this.name = name;
    }

    abstract void addToSet( String name, PropertySet set );

    public static void addToSet( final PropertySet set, final List<ImmutableProperty> data )
    {
        for ( ImmutableProperty datum : data )
        {
            datum.addToSet( datum.name, set );
        }
    }

    static ImmutableProperty ofNoValue( final String name, final ValueType<?> type )
    {
        return new ImmutablePropertyNoValue( name, type );
    }

    static ImmutableProperty ofValue( final String name, final List<Value> values )
    {
        switch ( values.size() )
        {
            case 0:
                throw new IllegalArgumentException( "values cannot be empty. ofNoValue must be used instead" );
            case 1:
                return new ImmutablePropertyValue1( name, values.get( 0 ) );
            default:
                return new ImmutablePropertyValueN( name, values );
        }
    }

    static ImmutableProperty ofValueSet( final String name, final List<ValueSet> values )
    {
        switch ( values.size() )
        {
            case 0:
                throw new IllegalArgumentException( "values cannot be empty. ofNoValue must be used instead" );
            case 1:
                return new ImmutablePropertyValueSet1( name, values.get( 0 ) );
            default:
                return new ImmutablePropertyValueSetN( name, values );
        }
    }

    static ValueSet nullValueSet()
    {
        return NULL_VALUE_SET;
    }

    static ValueSet toValueSet( final List<ImmutableProperty> set )
    {
        final int size = set.size();
        switch ( size )
        {
            case 0:
                return EMPTY_VALUE_SET;
            case 1:
                return new ValueSet1( set.get( 0 ) );
            default:
                return new ValueSetN( set );
        }
    }

    abstract static class ValueSet
    {
        public abstract void addValueSet( String name, PropertySet to );
    }

    private static class ImmutablePropertyNoValue
        extends ImmutableProperty
    {
        ValueType<?> type;

        ImmutablePropertyNoValue( final String name, final ValueType<?> type )
        {
            super( name );
            this.type = type;
        }

        public void addToSet( String name, PropertySet set )
        {
            set.ensureProperty( name, type );
        }
    }

    private static class ImmutablePropertyValueSetN
        extends ImmutableProperty
    {
        List<ValueSet> values;

        ImmutablePropertyValueSetN( final String name, final List<ValueSet> values )
        {
            super( name );
            this.values = List.copyOf( values );
        }

        public void addToSet( String name, PropertySet set )
        {
            for ( var value : values )
            {
                value.addValueSet( name, set );
            }
        }
    }

    private static class ImmutablePropertyValueSet1
        extends ImmutableProperty
    {
        ValueSet value;

        ImmutablePropertyValueSet1( final String name, ValueSet value )
        {
            super( name );
            this.value = value;
        }

        public void addToSet( String name, PropertySet set )
        {
            value.addValueSet( name, set );
        }
    }

    private static class ImmutablePropertyValueN
        extends ImmutableProperty
    {
        final List<Value> values;

        ImmutablePropertyValueN( final String name, final List<Value> values )
        {
            super( name );
            this.values = List.copyOf( values );
        }

        public void addToSet( String name, PropertySet set )
        {
            for ( Value value : values )
            {
                set.addProperty( name, value );
            }
        }
    }

    private static class ImmutablePropertyValue1
        extends ImmutableProperty
    {
        final Value value;

        ImmutablePropertyValue1( final String name, final Value value )
        {
            super( name );
            this.value = value;
        }

        public void addToSet( String name, PropertySet set )
        {
            set.addProperty( name, value );
        }
    }

    private static class ValueSet1
        extends ValueSet
    {
        final ImmutableProperty single;

        ValueSet1( final ImmutableProperty set )
        {
            this.single = set;
        }

        public void addValueSet( final String name, final PropertySet to )
        {
            final PropertySet propertySet = to.getTree().newSet();
            single.addToSet( single.name, propertySet );
            to.addSet( name, propertySet );
        }
    }

    private static class ValueSetN
        extends ValueSet
    {
        public final List<ImmutableProperty> set;

        /**
         * Used for NULL_VALUE_SET only.
         * This avoids null checks in the main constructor.
         */
        private ValueSetN()
        {
            this.set = null;
        }

        ValueSetN( final List<ImmutableProperty> set )
        {
            this.set = List.copyOf( set );
        }

        public void addValueSet( final String name, final PropertySet to )
        {
            if ( this.set == null )
            {
                to.addSet( name, null );
            }
            else
            {
                final PropertySet propertySet = to.getTree().newSet();
                ImmutableProperty.addToSet( propertySet, this.set );
                to.addSet( name, propertySet );
            }
        }
    }
}

