package com.enonic.xp.core.data;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.xp.core.util.BinaryReference;
import com.enonic.xp.core.util.GeoPoint;
import com.enonic.xp.core.util.Link;
import com.enonic.xp.core.util.Reference;


public final class PropertyTree
    implements PropertyIdProvider
{
    /**
     * A not Thread safe PropertyIdProvider that returns predictive ids. A int counter starting at 1.
     */
    public final static class PredictivePropertyIdProvider
        implements PropertyIdProvider
    {
        private int nextId = 1;

        @Override
        public PropertyId nextId()
        {
            // Note: Using UUID as value  takes 3.611 s for one million creations, compared to a counter which takes only 310 ms )
            return new PropertyId( String.valueOf( nextId++ ) );
        }
    }

    /**
     * Ensures an unique id for each Property without any dependencies.
     */
    public final static class DefaultPropertyIdProvider
        implements PropertyIdProvider
    {
        @Override
        public PropertyId nextId()
        {
            // Note: Using UUID as value  takes 3.611 s for one million creations, compared to a counter which takes only 310 ms )
            return new PropertyId( UUID.randomUUID().toString() );
        }
    }

    private final PropertyIdProvider idProvider;

    private final PropertySet root;

    private final LinkedHashMap<PropertyId, Property> propertyById = new LinkedHashMap<>();

    /**
     * Creates a new PropertyTree using a default PropertyIdProvider which uses UUID.randomUUID().
     */
    public PropertyTree()
    {
        idProvider = PropertyIdProviderAccessor.instance().get();
        root = new PropertySet( this );
    }

    public PropertyTree( final PropertyIdProvider idProvider )
    {
        this.idProvider = idProvider;
        root = new PropertySet( this );
    }

    PropertyTree( final PropertyTree source, final PropertyIdProvider idProvider )
    {
        this.idProvider = idProvider;
        root = source.getRoot().copy( this );
    }

    PropertyTree( final PropertySet source )
    {
        idProvider = source.getTree().getIdProvider();
        root = source.copy( this );
    }

    PropertyIdProvider getIdProvider()
    {
        return idProvider;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, Object> toMap()
    {
        return root.toMap();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PropertyTree ) )
        {
            return false;
        }

        final PropertyTree that = (PropertyTree) o;

        return Objects.equals( root, that.root );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( root );
    }

    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( "[\n" );
        for ( final PropertyArray propertyArray : root.getPropertyArrays() )
        {
            s.append( propertyArray.toString() ).append( "\n" );
        }
        s.append( "]" );
        return s.toString();
    }

    public PropertyTree copy()
    {
        return new PropertyTree( this, this.idProvider );
    }

    public Set<Property> getByValueType( final ValueType valueType )
    {
        final Set<Property> properties = Sets.newHashSet();

        for ( final PropertyId propertyId : propertyById.keySet() )
        {
            if ( propertyById.get( propertyId ).getType().equals( valueType ) )
            {
                properties.add( propertyById.get( propertyId ) );
            }
        }

        return properties;
    }

    /**
     * If called, the next add or set Property will be ignored if the value is null.
     */
    public PropertyTree ifNotNull()
    {
        root.ifNotNull();
        return this;
    }

    public PropertySet newSet()
    {
        return new PropertySet( this );
    }

    public PropertySet newSet( final PropertyTree source )
    {
        final PropertySet propertySet = new PropertySet( this );
        for ( final Property sourceProperty : source.getProperties() )
        {
            propertySet.addProperty( sourceProperty.getName(), sourceProperty.getValue() );
        }
        return propertySet;
    }

    public int countNames( final String name )
    {
        return root.countProperties( name );
    }

    public Property addProperty( final String name, final Value value )
    {
        return root.addProperty( name, value );
    }

    public Property setProperty( final String path, final Value value )
    {
        return setProperty( PropertyPath.from( path ), value );
    }

    public Property setProperty( final PropertyPath path, final Value value )
    {
        return root.setProperty( path, value );
    }

    public Property setProperty( final String name, final int index, final Value value )
    {
        return root.setProperty( name, index, value );
    }

    public void setValues( final String path, final Iterable<Value> values )
    {
        root.setValues( path, values );
    }

    public void setValues( final PropertyPath path, final Iterable<Value> values )
    {
        root.setValues( path, values );
    }

    public void removeProperty( final String path )
    {
        root.removeProperty( path );
    }

    public void removeProperty( final PropertyPath path )
    {
        root.removeProperty( path );
    }

    public void removeProperties( final String name )
    {
        root.removeProperties( name );
    }

    public PropertySet getRoot()
    {
        return root;
    }

    public boolean hasProperty( final String path )
    {
        return root.hasProperty( path );
    }

    public boolean hasProperty( final PropertyPath path )
    {
        return root.hasProperty( path );
    }

    public boolean hasProperty( final String name, final int index )
    {
        return root.hasProperty( name, index );
    }

    public Property getProperty( final PropertyId id )
    {
        return propertyById.get( id );
    }

    public Property getProperty( final String name, final int index )
    {
        return root.getProperty( name, index );
    }

    public Property getProperty( final PropertyPath path )
    {
        return root.getProperty( path );
    }

    public Property getProperty( final String path )
    {
        return root.getProperty( path );
    }

    public ImmutableList<Property> getProperties( final String name )
    {
        return root.getProperties( name );
    }

    public Iterable<Property> getProperties()
    {
        return root.getProperties();
    }

    public Value getValue( final String name, final int index )
    {
        return root.getValue( name, index );
    }

    public Value getValue( final PropertyPath path )
    {
        return root.getValue( path );
    }

    public Value getValue( final String path )
    {
        return root.getValue( path );
    }

    public Iterable<Value> getValues( final String name )
    {
        return root.getValues( name );
    }

    public PropertySet getPropertySet( final PropertyPath path )
    {
        return root.getPropertySet( path );
    }

    public PropertySet getPropertySet( final String path )
    {
        return root.getPropertySet( path );
    }

    public int getTotalSize()
    {
        return this.propertyById.size();
    }

    public PropertyId nextId()
    {
        return idProvider.nextId();
    }

    void registerProperty( final Property property )
    {
        Property previous = this.propertyById.put( property.getId(), property );
        if ( previous != null )
        {
            throw new IllegalStateException( "Duplicate Property id detected [" + property.getId() + "]! Existing Property \"" +
                                                 previous.getPath() + "\", new Property: \"" + property.getPath() + "\"" );
        }
    }

    void unregisterProperty( final PropertyId property )
    {
        this.propertyById.remove( property );
    }

    // Typed methods for creating a Property

    // setting PropertySet

    public Property setSet( final PropertyPath path, final PropertySet value )
    {
        return this.root.setSet( path, value );
    }

    public Property setSet( final String path, final PropertySet value )
    {
        return this.root.setSet( path, value );
    }

    public Property setSet( final String name, final int index, final PropertySet value )
    {
        return this.root.setSet( name, index, value );
    }

    public Property addSet( final String name, final PropertySet value )
    {
        return this.root.addSet( name, value );
    }

    public Property[] addSets( final String name, final PropertySet... value )
    {
        return this.root.addSets( name, value );
    }

    public PropertySet addSet( final String name )
    {
        return this.root.addSet( name );
    }

    // setting string

    public Property setString( final PropertyPath path, final String value )
    {
        return this.root.setString( path, value );
    }

    public Property setString( final String path, final String value )
    {
        return this.root.setString( path, value );
    }

    public Property setString( final String name, final int index, final String value )
    {
        return this.root.setString( name, index, value );
    }

    public Property addString( final String name, final String value )
    {
        return this.root.addString( name, value );
    }

    public Property[] addStrings( final String name, final String... value )
    {
        return this.root.addStrings( name, value );
    }

    public Property[] addStrings( final String name, final Collection<String> values )
    {
        return this.root.addStrings( name, values );
    }

    // setting html part

    public Property setHtmlPart( final PropertyPath path, final String value )
    {
        return this.root.setHtmlPart( path, value );
    }

    public Property setHtmlPart( final String path, final String value )
    {
        return this.root.setHtmlPart( path, value );
    }

    public Property setHtmlPart( final String name, final int index, final String value )
    {
        return this.root.setHtmlPart( name, index, value );
    }

    public Property addHtmlPart( final String name, final String value )
    {
        return this.root.addHtmlPart( name, value );
    }

    public Property[] addHtmlParts( final String name, final String... value )
    {
        return this.root.addHtmlParts( name, value );
    }

    // setting xml

    public Property setXml( final PropertyPath path, final String value )
    {
        return this.root.setXml( path, value );
    }

    public Property setXml( final String path, final String value )
    {
        return this.root.setXml( path, value );
    }

    public Property setXml( final String name, final int index, final String value )
    {
        return this.root.setXml( name, index, value );
    }

    public Property addXml( final String name, final String value )
    {
        return this.root.addXml( name, value );
    }

    public Property[] addXmls( final String name, final String... value )
    {
        return this.root.addXmls( name, value );
    }

    // setting binary

    public Property setBinaryReference( final PropertyPath path, final BinaryReference value )
    {
        return this.root.setBinaryReference( path, value );
    }

    public Property setBinaryReference( final String path, final BinaryReference value )
    {
        return this.root.setBinaryReference( path, value );
    }

    public Property setBinaryReference( final String name, final int index, final BinaryReference value )
    {
        return this.root.setBinaryReference( name, index, value );
    }

    public Property addBinaryReference( final String name, final BinaryReference value )
    {
        return this.root.addBinaryReference( name, value );
    }

    public Property[] addBinaryReferences( final String name, final BinaryReference... value )
    {
        return this.root.addBinaryReferences( name, value );
    }

    // setting reference

    public Property setReference( final PropertyPath path, final Reference value )
    {
        return this.root.setReference( path, value );
    }

    public Property setReference( final String path, final Reference value )
    {
        return this.root.setReference( path, value );
    }

    public Property setReference( final String name, final int index, final Reference value )
    {
        return this.root.setReference( name, index, value );
    }

    public Property addReference( final String name, final Reference value )
    {
        return this.root.addReference( name, value );
    }

    public Property[] addReferences( final String name, final Reference... value )
    {
        return this.root.addReferences( name, value );
    }

    // setting link

    public Property setLink( final PropertyPath path, final Link value )
    {
        return this.root.setLink( path, value );
    }

    public Property setLink( final String path, final Link value )
    {
        return this.root.setLink( path, value );
    }

    public Property setLink( final String name, final int index, final Link value )
    {
        return this.root.setLink( name, index, value );
    }

    public Property addLink( final String name, final Link value )
    {
        return this.root.addLink( name, value );
    }

    public Property[] addLinks( final String name, final Link... value )
    {
        return this.root.addLinks( name, value );
    }

    // setting boolean

    public Property setBoolean( final PropertyPath path, final Boolean value )
    {
        return this.root.setBoolean( path, value );
    }

    public Property setBoolean( final String path, final Boolean value )
    {
        return this.root.setBoolean( path, value );
    }

    public Property setBoolean( final String name, final int index, final Boolean value )
    {
        return this.root.setBoolean( name, index, value );
    }

    public Property addBoolean( final String name, final Boolean value )
    {
        return this.root.addBoolean( name, value );
    }

    public Property[] addBooleans( final String name, final Boolean... value )
    {
        return this.root.addBooleans( name, value );
    }

    // setting long

    public Property setLong( final PropertyPath path, final Long value )
    {
        return this.root.setLong( path, value );
    }

    public Property setLong( final String path, final Long value )
    {
        return this.root.setLong( path, value );
    }

    public Property setLong( final String name, final int index, final Long value )
    {
        return this.root.setLong( name, index, value );
    }

    public Property addLong( final String name, final Long value )
    {
        return this.root.addLong( name, value );
    }

    public Property[] addLongs( final String name, final Long... value )
    {
        return this.root.addLongs( name, value );
    }

    // setting double

    public Property setDouble( final PropertyPath path, final Double value )
    {
        return this.root.setDouble( path, value );
    }

    public Property setDouble( final String path, final Double value )
    {
        return this.root.setDouble( path, value );
    }

    public Property setDouble( final String name, final int index, final Double value )
    {
        return this.root.setDouble( name, index, value );
    }

    public Property addDouble( final String name, final Double value )
    {
        return this.root.addDouble( name, value );
    }

    public Property[] addDoubles( final String name, final Double... value )
    {
        return this.root.addDoubles( name, value );
    }

    // setting geo point

    public Property setGeoPoint( final PropertyPath path, final GeoPoint value )
    {
        return this.root.setGeoPoint( path, value );
    }

    public Property setGeoPoint( final String path, final GeoPoint value )
    {
        return this.root.setGeoPoint( path, value );
    }

    public Property setGeoPoint( final String name, final int index, final GeoPoint value )
    {
        return this.root.setGeoPoint( name, index, value );
    }

    public Property addGeoPoint( final String name, final GeoPoint value )
    {
        return this.root.addGeoPoint( name, value );
    }

    public Property[] addGeoPoints( final String name, final GeoPoint... value )
    {
        return this.root.addGeoPoints( name, value );
    }

    // setting local date

    public Property setLocalDate( final PropertyPath path, final LocalDate value )
    {
        return this.root.setLocalDate( path, value );
    }

    public Property setLocalDate( final String path, final LocalDate value )
    {
        return this.root.setLocalDate( path, value );
    }

    public Property setLocalDate( final String name, final int index, final LocalDate value )
    {
        return this.root.setLocalDate( name, index, value );
    }

    public Property addLocalDate( final String name, final LocalDate value )
    {
        return this.root.addLocalDate( name, value );
    }

    public Property[] addLocalDates( final String name, final LocalDate... value )
    {
        return this.root.addLocalDates( name, value );
    }

    // setting local date time

    public Property setLocalDateTime( final PropertyPath path, final LocalDateTime value )
    {
        return this.root.setLocalDateTime( path, value );
    }

    public Property setLocalDateTime( final String path, final LocalDateTime value )
    {
        return this.root.setLocalDateTime( path, value );
    }

    public Property setLocalDateTime( final String name, final int index, final LocalDateTime value )
    {
        return this.root.setLocalDateTime( name, index, value );
    }

    public Property addLocalDateTime( final String name, final LocalDateTime value )
    {
        return this.root.addLocalDateTime( name, value );
    }

    public Property[] addLocalDateTimes( final String name, final LocalDateTime... value )
    {
        return this.root.addLocalDateTimes( name, value );
    }

    // setting local time

    public Property setLocalTime( final PropertyPath path, final LocalTime value )
    {
        return this.root.setLocalTime( path, value );
    }

    public Property setLocalTime( final String path, final LocalTime value )
    {
        return this.root.setLocalTime( path, value );
    }

    public Property setLocalDateTime( final String name, final int index, final LocalTime value )
    {
        return this.root.setLocalTime( name, index, value );
    }

    public Property addLocalTime( final String name, final LocalTime value )
    {
        return this.root.addLocalTime( name, value );
    }

    public Property[] addLocalTimes( final String name, final LocalTime... value )
    {
        return this.root.addLocalTimes( name, value );
    }

    // setting instant

    public Property setInstant( final PropertyPath path, final Instant value )
    {
        return this.root.setInstant( path, value );
    }

    public Property setInstant( final String path, final Instant value )
    {
        return this.root.setInstant( path, value );
    }

    public Property setInstant( final String name, final int index, final Instant value )
    {
        return this.root.setInstant( name, index, value );
    }

    public Property addInstant( final String name, final Instant value )
    {
        return this.root.addInstant( name, value );
    }

    public Property[] addInstants( final String name, final Instant... value )
    {
        return this.root.addInstants( name, value );
    }

    // Typed methods for getting Property value

    // getting PropertySet

    public PropertySet getSet( final String name, final int index )
    {
        return this.root.getSet( name, index );
    }

    public PropertySet getSet( final PropertyPath path )
    {
        return this.root.getSet( path );
    }

    public PropertySet getSet( final String path )
    {
        return this.root.getSet( path );
    }

    public Iterable<PropertySet> getSets( final String name )
    {
        return this.root.getSets( name );
    }

    // getting string

    public String getString( final String name, final int index )
    {
        return this.root.getString( name, index );
    }

    public String getString( final PropertyPath path )
    {
        return this.root.getString( path );
    }

    public String getString( final String path )
    {
        return this.root.getString( path );
    }

    public Iterable<String> getStrings( final String name )
    {
        return this.root.getStrings( name );
    }

    // getting boolean

    public Boolean getBoolean( final String name, final int index )
    {
        return this.root.getBoolean( name, index );
    }

    public Boolean getBoolean( final PropertyPath path )
    {
        return this.root.getBoolean( path );
    }

    public Boolean getBoolean( final String path )
    {
        return this.root.getBoolean( path );
    }

    public Iterable<Boolean> getBooleans( final String name )
    {
        return this.root.getBooleans( name );
    }

    // getting long

    public Long getLong( final String name, final int index )
    {
        return this.root.getLong( name, index );
    }

    public Long getLong( final PropertyPath path )
    {
        return this.root.getLong( path );
    }

    public Long getLong( final String path )
    {
        return this.root.getLong( path );
    }

    public Iterable<Long> getLongs( final String name )
    {
        return this.root.getLongs( name );
    }

    // getting double

    public Double getDouble( final String name, final int index )
    {
        return this.root.getDouble( name, index );
    }

    public Double getDouble( final PropertyPath path )
    {
        return this.root.getDouble( path );
    }

    public Double getDouble( final String path )
    {
        return this.root.getDouble( path );
    }

    public Iterable<Double> getDoubles( final String name )
    {
        return this.root.getDoubles( name );
    }

    // getting geo point

    public GeoPoint getGeoPoint( final String name, final int index )
    {
        return this.root.getGeoPoint( name, index );
    }

    public GeoPoint getGeoPoint( final PropertyPath path )
    {
        return this.root.getGeoPoint( path );
    }

    public GeoPoint getGeoPoint( final String path )
    {
        return this.root.getGeoPoint( path );
    }

    public Iterable<GeoPoint> getGeoPoints( final String name )
    {
        return this.root.getGeoPoints( name );
    }

    // getting reference

    public BinaryReference getBinaryReference( final String name, final int index )
    {
        return this.root.getBinaryReference( name, index );
    }

    public BinaryReference getBinaryReference( final PropertyPath path )
    {
        return this.root.getBinaryReference( path );
    }

    public BinaryReference getBinaryReference( final String path )
    {
        return this.root.getBinaryReference( path );
    }

    public Iterable<BinaryReference> getBinaryReferences( final String name )
    {
        return this.root.getBinaryReferences( name );
    }

    // getting link

    public Link getLink( final String name, final int index )
    {
        return this.root.getLink( name, index );
    }

    public Link getLink( final PropertyPath path )
    {
        return this.root.getLink( path );
    }

    public Link getLink( final String path )
    {
        return this.root.getLink( path );
    }

    public Iterable<Link> getLinks( final String name )
    {
        return this.root.getLinks( name );
    }

    // getting binary

    // getting reference

    public Reference getReference( final String name, final int index )
    {
        return this.root.getReference( name, index );
    }

    public Reference getReference( final PropertyPath path )
    {
        return this.root.getReference( path );
    }

    public Reference getReference( final String path )
    {
        return this.root.getReference( path );
    }

    public Iterable<Reference> getReferences( final String name )
    {
        return this.root.getReferences( name );
    }

    // getting local date

    public LocalDate getLocalDate( final String name, final int index )
    {
        return this.root.getLocalDate( name, index );
    }

    public LocalDate getLocalDate( final PropertyPath path )
    {
        return this.root.getLocalDate( path );
    }

    public LocalDate getLocalDate( final String path )
    {
        return this.root.getLocalDate( path );
    }

    public Iterable<LocalDate> getLocalDates( final String name )
    {
        return this.root.getLocalDates( name );
    }

    // getting local date time

    public LocalDateTime getLocalDateTime( final String name, final int index )
    {
        return this.root.getLocalDateTime( name, index );
    }

    public LocalDateTime getLocalDateTime( final PropertyPath path )
    {
        return this.root.getLocalDateTime( path );
    }

    public LocalDateTime getLocalDateTime( final String path )
    {
        return this.root.getLocalDateTime( path );
    }

    public Iterable<LocalDateTime> getLocalDateTimes( final String name )
    {
        return this.root.getLocalDateTimes( name );
    }

    // getting local time

    public LocalTime getLocalTime( final String name, final int index )
    {
        return this.root.getLocalTime( name, index );
    }

    public LocalTime getLocalTime( final PropertyPath path )
    {
        return this.root.getLocalTime( path );
    }

    public LocalTime getLocalTime( final String path )
    {
        return this.root.getLocalTime( path );
    }

    public Iterable<LocalTime> getLocalTimes( final String name )
    {
        return this.root.getLocalTimes( name );
    }

    // getting instant

    public Instant getInstant( final String name, final int index )
    {
        return this.root.getInstant( name, index );
    }

    public Instant getInstant( final PropertyPath path )
    {
        return this.root.getInstant( path );
    }

    public Instant getInstant( final String path )
    {
        return this.root.getInstant( path );
    }

    public Iterable<Instant> getInstants( final String name )
    {
        return this.root.getInstants( name );
    }

}
