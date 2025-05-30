package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

@PublicApi
public final class PropertySet
{
    private PropertyTree tree;

    private Property property;

    private final Map<String, PropertyArray> propertyArrayByName;

    private boolean ifNotNull = false;

    PropertySet( final PropertyTree tree, final int initialCapacity )
    {
        this.tree = tree;
        this.propertyArrayByName = new LinkedHashMap<>( (int) Math.ceil( initialCapacity / 0.75D ) );
    }

    private PropertySet( final PropertySet source, final PropertyTree tree )
    {
        this.tree = tree;
        this.propertyArrayByName = new LinkedHashMap<>( (int) Math.ceil( source.propertyArrayByName.size() / 0.75D ) );
        for ( final PropertyArray array : source.propertyArrayByName.values() )
        {
            this.propertyArrayByName.put( array.getName(), array.copy( this ) );
        }
    }

    public PropertyTree getTree()
    {
        return this.tree;
    }

    public Property getProperty()
    {
        return property;
    }

    public Map<String, Object> toMap()
    {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<>( (int) Math.ceil( propertyArrayByName.size() / 0.75D ) );

        for ( var entry : this.propertyArrayByName.values() )
        {
            final String name = entry.getName();

            for ( final Property property : entry.getProperties() )
            {
                final Value value = property.getValue();
                if ( value.isPropertySet() )
                {
                    final PropertySet propertySet = property.getSet();
                    setMapValue( map, name, propertySet == null ? new LinkedHashMap<>() : propertySet.toMap() );
                }
                else
                {
                    setMapValue( map, name, property.getObject() );
                }
            }
        }

        return map;
    }

    private void setMapValue( final Map<String, Object> map, final String key, final Object value )
    {
        final Object rawValue = map.get( key );
        final ArrayList<Object> listValue;
        if ( rawValue == null )
        {
            listValue = new ArrayList<>();
            map.put( key, listValue );
        }
        else if ( rawValue instanceof ArrayList )
        {
            //noinspection unchecked
            listValue = (ArrayList<Object>) rawValue;
        }
        else
        {
            throw new IllegalStateException( "Expected ArrayList, unexpected type of value: " + rawValue.getClass().getName() );
        }

        listValue.add( value );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PropertySet ) )
        {
            return false;
        }

        final PropertySet that = (PropertySet) o;
        return this.propertyArrayByName.equals( that.propertyArrayByName );
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( "\n" );
        final Collection<PropertyArray> propertyArrays = propertyArrayByName.values();
        int size = propertyArrays.size();
        int count = 0;
        for ( PropertyArray propertyArray : propertyArrays )
        {
            s.append( propertyArray );
            if ( count++ < size - 1 )
            {
                s.append( ",\n" );
            }
        }

        return s.toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( propertyArrayByName );
    }

    public PropertySet copy( final PropertyTree tree )
    {
        return new PropertySet( this, tree );
    }

    public PropertyTree toTree()
    {
        return new PropertyTree( this );
    }

    void setProperty( final Property property )
    {
        this.property = property;
    }

    public PropertySet ifNotNull()
    {
        ifNotNull = true;
        return this;
    }

    // In future disallow to change parent tree
    // PropertySet should always be attached to a tree
    void setPropertyTree( final PropertyTree propertyTree )
    {
        if ( this.tree != null && propertyTree != this.tree )
        {
            throw new IllegalArgumentException(
                "PropertySet already belongs to another PropertyTree. Detach it from existing PropertyTree before adding it to another: " +
                    this.tree );
        }
        this.tree = propertyTree;
    }

    public int countProperties( final String name )
    {
        final PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null )
        {
            return 0;
        }

        long result = array.getProperties().stream().filter( ( p ) -> p.getValue() != null && p.getValue().getObject() != null ).count();
        return (int) result;
    }

    void addPropertyArray( final PropertyArray array )
    {
        this.propertyArrayByName.put( array.getName(), array );
    }

    private Property withPropertyArray( final String name, final Value value, Function<PropertyArray, Property> function )
    {
        if ( ifNotNull && value.isNull() )
        {
            ifNotNull = false;
            return null;
        }

        final ValueType type = value.getType();
        final PropertyArray array = getOrCreatePropertyArray( name, type, 1 );

        if ( tree != null )
        {
            final Object valueObject = value.getObject();
            if ( valueObject instanceof PropertySet )
            {
                ( (PropertySet) valueObject ).setPropertyTree( tree );
            }
        }
        return function.apply( array );
    }

    private PropertyArray getOrCreatePropertyArray( final String name, final ValueType type, final int initialCapacity )
    {
        PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null || ( array.size() == 0 && !array.getValueType().equals( type ) ) )
        {
            array = new PropertyArray( this, name, type, initialCapacity );
            this.propertyArrayByName.put( name, array );
        }
        return array;
    }

    public Property addProperty( final String name, final Value value )
    {
        return withPropertyArray( name, value, pa -> pa.addValue( value ) );
    }

    public Property setProperty( final String path, final Value value )
    {
        return setProperty( PropertyPath.from( path ), value );
    }

    public Property setProperty( final PropertyPath path, final Value value )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final String name = firstElement.getName();
            final int index = firstElement.getIndex();
            final Property existingProperty = getProperty( name, index );
            final PropertySet propertySet;
            if ( existingProperty == null )
            {
                propertySet = new PropertySet( tree, 1 );
                setProperty( name, index, ValueFactory.newPropertySet( propertySet ) );
            }
            else
            {
                propertySet = existingProperty.getSet();
            }
            return propertySet.setProperty( path.removeFirstPathElement(), value );
        }
        else
        {
            return setProperty( firstElement.getName(), firstElement.getIndex(), value );
        }
    }

    /**
     * WARNING: This method may be removed at any time. It is not recommended to use it in new code.
     * This method is only used to simulate the behavior of PropertySet Serialisation.
     * See {@link com.enonic.xp.data.PropertyTreeJson#fromJson}
     * <br>
     * <br>
     * Ensures that the property with the given name has type of valueType given.
     * If the property does not exist, empty property (without values) will be created.
     * If the property with values exists and is not a type of valueType given, an exception will be thrown.
     * Difference with setProperty is that this method does not set the property value.
     *
     * @param name      property name
     * @param valueType expected value type
     */
    public void ensureProperty( final String name, final ValueType valueType )
    {
        final PropertyArray array = getOrCreatePropertyArray( name, valueType, 0 );
        array.checkType( valueType );
    }

    public Property setProperty( final String name, final int index, final Value value )
    {
        return withPropertyArray( name, value, pa -> pa.setValue( index, value ) );
    }

    public void setValues( final String path, final Iterable<Value> values )
    {
        setValues( PropertyPath.from( path ), values );
    }

    public void setValues( final PropertyPath path, final Iterable<Value> values )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return;
            }
            final PropertySet set = property.getSet();
            set.setValues( path.removeFirstPathElement(), values );
        }
        else
        {
            removeProperties( firstElement.getName() );
            int index = 0;
            for ( final Value value : values )
            {
                setProperty( firstElement.getName(), index++, value );
            }
        }
    }

    public void removeProperty( final String path )
    {
        removeProperty( PropertyPath.from( path ) );
    }

    public void removeProperty( final PropertyPath path )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return;
            }
            final PropertySet set = property.getSet();
            set.removeProperty( path.removeFirstPathElement() );
        }
        else
        {
            final PropertyArray propertyArray = propertyArrayByName.get( firstElement.getName() );
            if ( propertyArray == null )
            {
                return;
            }

            propertyArray.remove( firstElement.getIndex() );
        }
    }

    public void removeProperties( final String name )
    {
        propertyArrayByName.remove( name );
    }

    public boolean hasProperty( final String path )
    {
        return hasProperty( PropertyPath.from( path ) );
    }

    public boolean hasProperty( final PropertyPath path )
    {
        return getProperty( path ) != null;
    }

    public boolean hasProperty( final String name, final int index )
    {
        return getProperty( name, index ) != null;
    }

    public Property getProperty( final String path )
    {
        return getProperty( PropertyPath.from( path ) );
    }

    public Property getProperty( final PropertyPath path )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return null;
            }
            final PropertySet set = property.getSet();
            return set.getProperty( path.removeFirstPathElement() );
        }
        else
        {
            return getProperty( firstElement );
        }
    }

    public Property getProperty( final String name, final int index )
    {
        Property.checkName( name );
        final PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null )
        {
            return null;
        }
        return array.get( index );
    }

    public boolean isNull( final String path )
    {
        return isNull( PropertyPath.from( path ) );
    }

    public boolean isNull( final PropertyPath path )
    {
        return !isNotNull( path );
    }

    public boolean isNotNull( final String path )
    {
        return isNotNull( PropertyPath.from( path ) );
    }

    public boolean isNotNull( final PropertyPath path )
    {
        final Property property = getProperty( path );
        return property != null && !property.hasNullValue();
    }

    public String[] getPropertyNames()
    {
        return propertyArrayByName.keySet().toArray( String[]::new );
    }

    private Property getProperty( final PropertyPath.Element element )
    {
        return getProperty( element.getName(), element.getIndex() );
    }

    public List<Property> getProperties( final String name )
    {
        Property.checkName( name );
        final PropertyArray propertyArray = this.propertyArrayByName.get( name );
        if ( propertyArray == null )
        {
            return ImmutableList.of();
        }

        return propertyArray.getProperties();
    }

    public Iterable<Property> getProperties()
    {
        final ImmutableList.Builder<Property> builder = new ImmutableList.Builder<>();
        for ( final PropertyArray propertyArray : this.propertyArrayByName.values() )
        {
            propertyArray.getProperties().forEach( builder::add );
        }
        return builder.build();
    }

    public List<Property> getProperties( ValueType valueType )
    {
        final ImmutableList.Builder<Property> builder = new ImmutableList.Builder<>();
        for ( final PropertyArray propertyArray : this.propertyArrayByName.values() )
        {
            for ( final Property property : propertyArray.getProperties() )
            {
                if ( property.getType().equals( valueType ) )
                {
                    builder.add( property );
                }

                final Value propertyValue = property.getValue();
                if ( propertyValue instanceof PropertySetValue )
                {
                    final Object valueObject = propertyValue.getObject();
                    if ( valueObject instanceof PropertySet )
                    {
                        builder.addAll( ( (PropertySet) valueObject ).getProperties( valueType ) );
                    }
                }
            }
        }
        return builder.build();
    }

    public int getPropertySize()
    {
        int propertySize = 0;
        for ( final PropertyArray propertyArray : this.propertyArrayByName.values() )
        {
            propertySize += propertyArray.size();
            for ( final Property property : propertyArray.getProperties() )
            {
                if ( property.getValue() instanceof PropertySetValue )
                {
                    propertySize += ( (PropertySet) property.getValue().getObject() ).getPropertySize();
                }
            }
        }
        return propertySize;

    }

    public Collection<PropertyArray> getPropertyArrays()
    {
        return this.propertyArrayByName.values();
    }

    PropertyArray getPropertyArray( final String name )
    {
        return this.propertyArrayByName.get( name );
    }

    public Value getValue( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue() : null;
    }

    public Value getValue( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue() : null;
    }

    public Value getValue( final String path )
    {
        return getValue( PropertyPath.from( path ) );
    }

    public Iterable<Value> getValues( final String name )
    {
        final ImmutableList.Builder<Value> valueBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            valueBuilder.add( property.getValue() );
        }
        return valueBuilder.build();
    }

    public PropertySet getPropertySet( final PropertyPath path )
    {
        final Property property = getProperty( path );
        if ( property == null )
        {
            return null;
        }
        return property.getValue().asData();
    }

    public PropertySet getPropertySet( final String path )
    {
        return getPropertySet( PropertyPath.from( path ) );
    }

    // Typed methods for creating a Property

    // setting set
    public Property setSet( final String path, final PropertySet value )
    {
        return this.setSet( PropertyPath.from( path ), value );
    }

    public Property setSet( final PropertyPath path, final PropertySet value )
    {
        return this.setProperty( path, ValueFactory.newPropertySet( value ) );
    }

    public Property setSet( final String name, final int index, final PropertySet value )
    {
        return this.setProperty( name, index, ValueFactory.newPropertySet( value ) );
    }

    public Property addSet( final String name, final PropertySet value )
    {
        return this.addProperty( name, ValueFactory.newPropertySet( value ) );
    }

    public Property[] addSets( final String name, final PropertySet... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newPropertySet( values[i] ) );
        }
        return properties;
    }

    public PropertySet addSet( final String name )
    {
        final PropertySet propertySet = new PropertySet( tree, 0 );
        addSet( name, propertySet );
        return propertySet;
    }

    // setting string
    public Property setString( final String path, final String value )
    {
        return this.setString( PropertyPath.from( path ), value );
    }

    public Property setString( final PropertyPath path, final String value )
    {
        return this.setProperty( path, ValueFactory.newString( value ) );
    }

    public Property setString( final String name, final int index, final String value )
    {
        return this.setProperty( name, index, ValueFactory.newString( value ) );
    }

    public Property addString( final String name, final String value )
    {
        return this.addProperty( name, ValueFactory.newString( value ) );
    }

    public Property[] addStrings( final String name, final String... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newString( values[i] ) );
        }
        return properties;
    }

    public Property[] addStrings( final String name, final Collection<String> values )
    {
        return values.stream().map( ( value ) -> addProperty( name, ValueFactory.newString( value ) ) ).toArray( Property[]::new );
    }

    // setting xml

    public Property setXml( final String path, final String value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newXml( value ) );
    }

    public Property setXml( final PropertyPath path, final String value )
    {
        return this.setProperty( path, ValueFactory.newXml( value ) );
    }

    public Property setXml( final String name, final int index, final String value )
    {
        return this.setProperty( name, index, ValueFactory.newXml( value ) );
    }

    public Property addXml( final String name, final String value )
    {
        return this.addProperty( name, ValueFactory.newXml( value ) );
    }

    public Property[] addXmls( final String name, final String... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newXml( values[i] ) );
        }
        return properties;
    }

    // setting boolean

    public Property setBoolean( final String path, final Boolean value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newBoolean( value ) );
    }

    public Property setBoolean( final PropertyPath path, final Boolean value )
    {
        return this.setProperty( path, ValueFactory.newBoolean( value ) );
    }

    public Property setBoolean( final String name, final int index, final Boolean value )
    {
        return this.setProperty( name, index, ValueFactory.newBoolean( value ) );
    }

    public Property addBoolean( final String name, final Boolean value )
    {
        return this.addProperty( name, ValueFactory.newBoolean( value ) );
    }

    public Property[] addBooleans( final String name, final Boolean... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newBoolean( values[i] ) );
        }
        return properties;
    }

    // setting long

    public Property setLong( final String path, final Long value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newLong( value ) );
    }

    public Property setLong( final PropertyPath path, final Long value )
    {
        return this.setProperty( path, ValueFactory.newLong( value ) );
    }

    public Property setLong( final String name, final int index, final Long value )
    {
        return this.setProperty( name, index, ValueFactory.newLong( value ) );
    }

    public Property addLong( final String name, final Long value )
    {
        return this.addProperty( name, ValueFactory.newLong( value ) );
    }

    public Property[] addLongs( final String name, final Long... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newLong( values[i] ) );
        }
        return properties;
    }

    // setting local date

    public Property setLocalDate( final String path, final LocalDate value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newLocalDate( value ) );
    }

    public Property setLocalDate( final PropertyPath path, final LocalDate value )
    {
        return this.setProperty( path, ValueFactory.newLocalDate( value ) );
    }

    public Property setLocalDate( final String name, final int index, final LocalDate value )
    {
        return this.setProperty( name, index, ValueFactory.newLocalDate( value ) );
    }

    public Property addLocalDate( final String name, final LocalDate value )
    {
        return this.addProperty( name, ValueFactory.newLocalDate( value ) );
    }

    public Property[] addLocalDates( final String name, final LocalDate... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newLocalDate( values[i] ) );
        }
        return properties;
    }

    // setting local date time

    public Property setLocalDateTime( final String path, final LocalDateTime value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newLocalDateTime( value ) );
    }

    public Property setLocalDateTime( final PropertyPath path, final LocalDateTime value )
    {
        return this.setProperty( path, ValueFactory.newLocalDateTime( value ) );
    }

    public Property setLocalDateTime( final String name, final int index, final LocalDateTime value )
    {
        return this.setProperty( name, index, ValueFactory.newLocalDateTime( value ) );
    }

    public Property addLocalDateTime( final String name, final LocalDateTime value )
    {
        return this.addProperty( name, ValueFactory.newLocalDateTime( value ) );
    }

    public Property[] addLocalDateTimes( final String name, final LocalDateTime... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newLocalDateTime( values[i] ) );
        }
        return properties;
    }

    // setting local time

    public Property setLocalTime( final String path, final LocalTime value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newLocalTime( value ) );
    }

    public Property setLocalTime( final PropertyPath path, final LocalTime value )
    {
        return this.setProperty( path, ValueFactory.newLocalTime( value ) );
    }

    public Property setLocalTime( final String name, final int index, final LocalTime value )
    {
        return this.setProperty( name, index, ValueFactory.newLocalTime( value ) );
    }

    public Property addLocalTime( final String name, final LocalTime value )
    {
        return this.addProperty( name, ValueFactory.newLocalTime( value ) );
    }

    public Property[] addLocalTimes( final String name, final LocalTime... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newLocalTime( values[i] ) );
        }
        return properties;
    }

    // setting instant

    public Property setInstant( final String path, final Instant value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newDateTime( value ) );
    }

    public Property setInstant( final PropertyPath path, final Instant value )
    {
        return this.setProperty( path, ValueFactory.newDateTime( value ) );
    }

    public Property setInstant( final String name, final int index, final Instant value )
    {
        return this.setProperty( name, index, ValueFactory.newDateTime( value ) );
    }

    public Property addInstant( final String name, final Instant value )
    {
        return this.addProperty( name, ValueFactory.newDateTime( value ) );
    }

    public Property[] addInstants( final String name, final Instant... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newDateTime( values[i] ) );
        }
        return properties;
    }

    // setting double

    public Property setDouble( final String path, final Double value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newDouble( value ) );
    }

    public Property setDouble( final PropertyPath path, final Double value )
    {
        return this.setProperty( path, ValueFactory.newDouble( value ) );
    }

    public Property setDouble( final String name, final int index, final Double value )
    {
        return this.setProperty( name, index, ValueFactory.newDouble( value ) );
    }

    public Property addDouble( final String name, final Double value )
    {
        return this.addProperty( name, ValueFactory.newDouble( value ) );
    }

    public Property[] addDoubles( final String name, final Double... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newDouble( values[i] ) );
        }
        return properties;
    }

    // setting geo point

    public Property setGeoPoint( final String path, final GeoPoint value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newGeoPoint( value ) );
    }

    public Property setGeoPoint( final PropertyPath path, final GeoPoint value )
    {
        return this.setProperty( path, ValueFactory.newGeoPoint( value ) );
    }

    public Property setGeoPoint( final String name, final int index, final GeoPoint value )
    {
        return this.setProperty( name, index, ValueFactory.newGeoPoint( value ) );
    }

    public Property addGeoPoint( final String name, final GeoPoint value )
    {
        return this.addProperty( name, ValueFactory.newGeoPoint( value ) );
    }

    public Property[] addGeoPoints( final String name, final GeoPoint... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newGeoPoint( values[i] ) );
        }
        return properties;
    }

    // setting reference
    public Property setReference( final String path, final Reference value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newReference( value ) );
    }

    public Property setReference( final PropertyPath path, final Reference value )
    {
        return this.setProperty( path, ValueFactory.newReference( value ) );
    }

    public Property setReference( final String name, final int index, final Reference value )
    {
        return this.setProperty( name, index, ValueFactory.newReference( value ) );
    }

    public Property addReference( final String name, final Reference value )
    {
        return this.addProperty( name, ValueFactory.newReference( value ) );
    }

    public Property[] addReferences( final String name, final Reference... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newReference( values[i] ) );
        }
        return properties;
    }

    // setting reference
    public Property setBinaryReference( final String path, final BinaryReference value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newBinaryReference( value ) );
    }

    public Property setBinaryReference( final PropertyPath path, final BinaryReference value )
    {
        return this.setProperty( path, ValueFactory.newBinaryReference( value ) );
    }

    public Property setBinaryReference( final String name, final int index, final BinaryReference value )
    {
        return this.setProperty( name, index, ValueFactory.newBinaryReference( value ) );
    }

    public Property addBinaryReference( final String name, final BinaryReference value )
    {
        return this.addProperty( name, ValueFactory.newBinaryReference( value ) );
    }

    public Property[] addBinaryReferences( final String name, final BinaryReference... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newBinaryReference( values[i] ) );
        }
        return properties;
    }


    // setting link
    public Property setLink( final String path, final Link value )
    {
        return this.setProperty( PropertyPath.from( path ), ValueFactory.newLink( value ) );
    }

    public Property setLink( final PropertyPath path, final Link value )
    {
        return this.setProperty( path, ValueFactory.newLink( value ) );
    }

    public Property setLink( final String name, final int index, final Link value )
    {
        return this.setProperty( name, index, ValueFactory.newLink( value ) );
    }

    public Property addLink( final String name, final Link value )
    {
        return this.addProperty( name, ValueFactory.newLink( value ) );
    }

    public Property[] addLinks( final String name, final Link... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, ValueFactory.newLink( values[i] ) );
        }
        return properties;
    }

    // Typed methods for getting Property value

    // getting property set

    public PropertySet getSet( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getSet() : null;
    }

    public PropertySet getSet( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getSet() : null;
    }

    public PropertySet getSet( final String path )
    {
        return getSet( PropertyPath.from( path ) );
    }

    public Iterable<PropertySet> getSets( final String name )
    {
        return getFilteredValues( name, Property::getSet );
    }

    // getting string

    public String getString( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asString() : null;
    }

    public String getString( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asString() : null;
    }

    public String getString( final String path )
    {
        return getString( PropertyPath.from( path ) );
    }

    public Iterable<String> getStrings( final String name )
    {
        return getFilteredValues( name, Property::getString );
    }

    // getting boolean

    public Boolean getBoolean( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asBoolean() : null;
    }

    public Boolean getBoolean( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asBoolean() : null;
    }

    public Boolean getBoolean( final String path )
    {
        return getBoolean( PropertyPath.from( path ) );
    }

    public Iterable<Boolean> getBooleans( final String name )
    {
        return getFilteredValues( name, Property::getBoolean );
    }

    // getting long

    public Long getLong( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asLong() : null;
    }

    public Long getLong( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asLong() : null;
    }

    public Long getLong( final String path )
    {
        return getLong( PropertyPath.from( path ) );
    }

    public Iterable<Long> getLongs( final String name )
    {
        return getFilteredValues( name, Property::getLong );
    }

    // getting double

    public Double getDouble( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asDouble() : null;
    }

    public Double getDouble( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asDouble() : null;
    }

    public Double getDouble( final String path )
    {
        return getDouble( PropertyPath.from( path ) );
    }

    public Iterable<Double> getDoubles( final String name )
    {
        return getFilteredValues( name, Property::getDouble );
    }

    // getting geo point

    public GeoPoint getGeoPoint( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asGeoPoint() : null;
    }

    public GeoPoint getGeoPoint( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asGeoPoint() : null;
    }

    public GeoPoint getGeoPoint( final String path )
    {
        return getGeoPoint( PropertyPath.from( path ) );
    }

    public Iterable<GeoPoint> getGeoPoints( final String name )
    {
        return getFilteredValues( name, Property::getGeoPoint );
    }

    // getting reference

    public Reference getReference( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asReference() : null;
    }

    public Reference getReference( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asReference() : null;
    }

    public Reference getReference( final String path )
    {
        return getReference( PropertyPath.from( path ) );
    }

    public Iterable<Reference> getReferences( final String name )
    {
        return getFilteredValues( name, Property::getReference );
    }

    private <T> Iterable<T> getFilteredValues( final String name, final Function<Property, T> map )
    {
        return getProperties( name ).stream().filter( p -> !p.hasNullValue() ).map( map ).collect( Collectors.toList() );
    }

    // getting binary

    public BinaryReference getBinaryReference( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asBinaryReference() : null;
    }

    public BinaryReference getBinaryReference( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asBinaryReference() : null;
    }

    public BinaryReference getBinaryReference( final String path )
    {
        return getBinaryReference( PropertyPath.from( path ) );
    }

    public Iterable<BinaryReference> getBinaryReferences( final String name )
    {
        return getFilteredValues( name, Property::getBinaryReference );
    }

    // getting link

    public Link getLink( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asLink() : null;
    }

    public Link getLink( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asLink() : null;
    }

    public Link getLink( final String path )
    {
        return getLink( PropertyPath.from( path ) );
    }

    public Iterable<Link> getLinks( final String name )
    {
        return getFilteredValues( name, Property::getLink );
    }

    // getting local date

    public LocalDate getLocalDate( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asLocalDate() : null;
    }

    public LocalDate getLocalDate( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asLocalDate() : null;
    }

    public LocalDate getLocalDate( final String path )
    {
        return getLocalDate( PropertyPath.from( path ) );
    }

    public Iterable<LocalDate> getLocalDates( final String name )
    {
        return getFilteredValues( name, Property::getLocalDate );
    }

    // getting local date time

    public LocalDateTime getLocalDateTime( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getLocalDateTime() : null;
    }

    public LocalDateTime getLocalDateTime( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getLocalDateTime() : null;
    }

    public LocalDateTime getLocalDateTime( final String path )
    {
        return getLocalDateTime( PropertyPath.from( path ) );
    }

    public Iterable<LocalDateTime> getLocalDateTimes( final String name )
    {
        return getFilteredValues( name, Property::getLocalDateTime );
    }

    // getting local time

    public LocalTime getLocalTime( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getLocalTime() : null;
    }

    public LocalTime getLocalTime( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getLocalTime() : null;
    }

    public LocalTime getLocalTime( final String path )
    {
        return getLocalTime( PropertyPath.from( path ) );
    }

    public Iterable<LocalTime> getLocalTimes( final String name )
    {
        return getFilteredValues( name, Property::getLocalTime );
    }

    // getting instant

    public Instant getInstant( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getInstant() : null;
    }

    public Instant getInstant( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getInstant() : null;
    }

    public Instant getInstant( final String path )
    {
        return getInstant( PropertyPath.from( path ) );
    }

    public Iterable<Instant> getInstants( final String name )
    {
        return getFilteredValues( name, Property::getInstant );
    }

    // getting / setting enums

    public <T extends Enum<T>> T getEnum( final String name, final int index, Class<T> enumClass )
    {
        return stringToEnum( getString( name, index ), enumClass );
    }

    public <T extends Enum<T>> T getEnum( final PropertyPath path, Class<T> enumClass )
    {
        return stringToEnum( getString( path ), enumClass );
    }

    public <T extends Enum<T>> T getEnum( final String path, Class<T> enumClass )
    {
        return stringToEnum( getString( path ), enumClass );
    }

    public Property setEnum( final String path, final Enum value )
    {
        return this.setString( path, enumToString( value ) );
    }

    public Property setEnum( final PropertyPath path, final Enum value )
    {
        return this.setString( path, enumToString( value ) );
    }

    public Property setEnum( final String name, final int index, final Enum value )
    {
        return this.setString( name, index, enumToString( value ) );
    }

    public Property addEnum( final String name, final Enum value )
    {
        return this.addString( name, enumToString( value ) );
    }

    private <T extends Enum<T>> T stringToEnum( final String value, Class<T> enumClass )
    {
        if ( value == null )
        {
            return null;
        }
        return Enum.valueOf( enumClass, value );
    }

    private String enumToString( final Enum e )
    {
        if ( e == null )
        {
            return null;
        }
        return e.toString();
    }

    static void translate( final Map<String, ?> json, final PropertySet into )
    {
        for ( Map.Entry<String, ?> field : json.entrySet() )
        {
            addValue( into, field.getKey(), field.getValue() );
        }
    }

    private static void addValue( final PropertySet parent, final String key, final Object value )
    {
        if ( value instanceof Collection )
        {
            for ( final Object objNode : (Collection<?>) value )
            {
                addValue( parent, key, objNode );
            }
        }
        else if ( value instanceof Map )
        {
            final PropertySet parentSet = parent.addSet( key );
            for ( final Map.Entry<String, ?> next : ( (Map<String, ?>) value ).entrySet() )
            {
                addValue( parentSet, next.getKey(), next.getValue() );
            }
        }
        else
        {
            parent.addProperty( key, resolveCoreValue( value ) );
        }
    }

    private static Value resolveCoreValue( final Object value )
    {
        return switch ( value )
        {
            case String s -> ValueFactory.newString( s );
            case Integer i -> ValueFactory.newLong( i.longValue() );
            case Float v -> ValueFactory.newDouble( v.doubleValue() );
            case Double v -> ValueFactory.newDouble( v );
            case Boolean b -> ValueFactory.newBoolean( b );
            case Long l -> ValueFactory.newLong( l );
            case Byte b -> ValueFactory.newLong( b.longValue() );
            case Short s -> ValueFactory.newLong( s.longValue() );
            case Instant instant -> ValueFactory.newDateTime( instant );
            case Date date -> ValueFactory.newDateTime( date.toInstant() );
            case LocalTime localTime -> ValueFactory.newLocalTime( localTime );
            case LocalDateTime localDateTime -> ValueFactory.newLocalDateTime( localDateTime );
            case LocalDate localDate -> ValueFactory.newLocalDate( localDate );
            case GeoPoint geoPoint -> ValueFactory.newGeoPoint( geoPoint );
            case Reference reference -> ValueFactory.newReference( reference );
            case BinaryReference binaryReference -> ValueFactory.newBinaryReference( binaryReference );
            case Link link -> ValueFactory.newLink( link );
            case null -> ValueFactory.newString( null );
            default -> ValueFactory.newString( value.toString() );
        };
    }
}
