package com.enonic.wem.api.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.type.ValueType;

@SuppressWarnings("UnusedDeclaration")
public class DataSet
    extends Data<DataSet>
    implements Iterable<Data>
{
    private LinkedHashMap<DataId, Data> dataById = new LinkedHashMap<>();

    private Map<String, DataArray> arrayByDataName = new HashMap<>();

    protected DataSet()
    {
        // Creates a root DataSet
    }

    public DataSet( final String name )
    {
        super( name );
    }

    protected DataSet( final Builder builder )
    {
        super( builder.name );

        for ( Data data : builder.dataList )
        {
            add( data );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder newDataSet( final DataSet dataSet )
    {
        return new Builder( dataSet );
    }

    public static Builder newDataSet( final String name )
    {
        final Builder builder = new Builder();
        builder.name( name );
        return builder;
    }

    public boolean isRoot()
    {
        return false;
    }

    /**
     * Adds the given Data to this DataSet. If entries with same name already exists, then it will be positioned last in the array.
     */
    public final void add( final Data data )
    {
        Preconditions.checkNotNull( data, "data to add not given" );
        doAdd( data );
    }

    public final void addAll( final Iterable<Data> datas )
    {
        for ( final Data data : datas )
        {
            doAdd( data );
        }
    }

    /**
     * Adds the given Value at given path. If the dataPath contains DataSet, then it will be created if not already existing.
     *
     * @return the added Property.
     */
    public final Property addProperty( final String dataPath, final Value value )
    {
        return addProperty( DataPath.from( dataPath ), value );
    }

    /**
     * Adds the given Value at given path. If the dataPath contains DataSet, then it will be created if not already existing.
     *
     * @return the added Property.
     */
    public final Property addProperty( final DataPath dataPath, final Value value )
    {
        if ( dataPath.elementCount() > 1 )
        {
            final DataSet dataSet = findOrCreateDataSet( DataId.from( dataPath.getFirstElement() ) );
            return dataSet.addProperty( dataPath.removeFirstPathElement(), value );
        }
        else
        {
            final Property property = Property.newProperty( dataPath.getFirstElement().getName(), value );
            doAdd( property );
            return property;
        }
    }

    private DataSet findOrCreateDataSet( final DataId dataId )
    {
        final Data exData = dataById.get( dataId );
        if ( exData == null )
        {
            final DataSet dataSet = create().name( dataId.getName() ).build();
            doAdd( dataSet );
            return dataSet;
        }
        else
        {
            return exData.toDataSet();
        }
    }

    private void doAdd( final Data data )
    {
        if ( data.getParent() != null )
        {
            throw new IllegalArgumentException(
                "Data [" + data.getName() + "] already added to another parent: " + data.getParent().getPath().toString() );
        }
        data.setParent( this );
        data.setArrayIndex( nameCount( data.getName() ) );
        registerArray( data );
        dataById.put( data.getDataId(), data );
    }

    public final Property[] setProperty( final String path, final Value... values )
    {
        return setProperty( DataPath.from( path ), values );
    }

    public final void remove( final DataId dataId )
    {
        final Data data = dataById.get( dataId );
        if ( data != null )
        {
            final DataArray dataArray = arrayByDataName.get( data.getName() );
            if ( dataArray != null )
            {
                for ( int i = 0; i < dataArray.size(); i++ )
                {
                    final Data dataArrayItem = dataArray.getData( i );
                    dataById.remove( dataArrayItem.getDataId() );
                }
            }
            arrayByDataName.remove( data.getName() );
            dataById.remove( dataId );
        }
    }

    public final Property setProperty( final DataId dataId, final Value value )
    {
        return doSetProperty( dataId, value );
    }

    public final Property[] setProperty( final DataPath path, final Value... values )
    {
        if ( path.elementCount() > 1 )
        {
            final DataSet dataSet = findOrCreateDataSet( DataId.from( path.getFirstElement() ) );
            return dataSet.setProperty( path.removeFirstPathElement(), values );
        }
        else
        {
            Property[] properties = new Property[values.length];
            Preconditions.checkArgument( values.length > 0, "No values given for path: %s", path.toString() );
            if ( values.length == 1 )
            {
                properties[0] = doSetProperty( DataId.from( path.getFirstElement() ), values[0] );
            }
            else
            {
                if ( path.getFirstElement().hasIndex() )
                {
                    Preconditions.checkArgument( path.getFirstElement().getIndex() > 0,
                                                 "Cannot set array at to another starting index than zero: %s", path.toString() );
                }

                for ( int i = 0; i < values.length; i++ )
                {
                    properties[i] = doSetProperty( DataId.from( path.getFirstElement().getName(), i ), values[i] );
                }
            }
            return properties;
        }
    }

    private Property doSetProperty( final DataId dataId, final Value value )
    {
        Preconditions.checkNotNull( value, "No value given for Data: %s", dataId );
        final Data exData = dataById.get( dataId );

        if ( exData == null )
        {
            final int expectedIndex = nameCount( dataId.getName() );
            if ( dataId.getIndex() != expectedIndex )
            {
                throw new IllegalArgumentException(
                    "Property [" + dataId + "] expected to be given a successive index [" + expectedIndex + "]: " +
                        dataId.getIndex() );
            }

            final Property newProperty = Property.newProperty( dataId.getName(), value );
            newProperty.setParent( this );
            newProperty.setArrayIndex( dataId.getIndex() );

            registerArray( newProperty );

            dataById.put( dataId, newProperty );
            return newProperty;
        }
        else
        {
            final Property existingProperty = exData.toProperty();
            existingProperty.setValue( value );
            return existingProperty;
        }
    }

    private void registerArray( final Data newData )
    {
        DataArray array = arrayByDataName.get( newData.getName() );
        if ( array == null )
        {
            if ( newData.isProperty() )
            {
                final Property newProperty = newData.toProperty();
                array = PropertyArray.newPropertyArray().name( newData.getName() ).propertyType( newProperty.getValueType() ).parent(
                    this ).build();
            }
            else
            {
                array = DataSetArray.newDataSetArray().name( newData.getName() ).parent( this ).build();
            }
            arrayByDataName.put( newData.getName(), array );
        }

        array.add( newData );
    }

    public final int size()
    {
        return dataById.size();
    }

    public final Iterator<Data> iterator()
    {
        return dataById.values().iterator();
    }

    public final Iterable<String> getDataNames()
    {
        return arrayByDataName.keySet();
    }

    public final int nameCount( final String dataName )
    {
        DataPath.Element.checkName( dataName );
        DataArray array = arrayByDataName.get( dataName );
        if ( array == null )
        {
            return 0;
        }
        return array.size();
    }

    public final Data getData( final String path )
    {
        return getData( DataPath.from( path ) );
    }

    public final Data getData( final DataId id )
    {
        return doGetData( id );
    }

    public final Data getData( final DataPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return doForwardGetData( path );
        }
        else
        {
            return doGetData( DataId.from( path.getLastElement() ) );
        }
    }

    private Data doForwardGetData( final DataPath path )
    {
        final Data data = dataById.get( DataId.from( path.getFirstElement() ) );
        if ( data == null )
        {
            return null;
        }

        return data.toDataSet().getData( path.removeFirstPathElement() );
    }

    private Data doGetData( final DataId dataId )
    {
        final Data data = dataById.get( dataId );
        if ( data == null )
        {
            return null;
        }

        return data;
    }

    public final List<Data> getDataByName( final String dataName )
    {
        DataPath.Element.checkName( dataName );
        final DataArray array = arrayByDataName.get( dataName );
        return array != null ? array.asList() : new ArrayList<>();
    }

    public boolean hasData( final String dataId )
    {
        return this.dataById.containsKey( DataId.from( dataId ) );
    }

    public boolean hasData( final DataId dataId )
    {
        return this.dataById.containsKey( dataId );
    }

    /**
     * Returns a ImmutableList of the Data-s in this DataSet.
     */
    public ImmutableList<Data> getData()
    {
        final ImmutableList.Builder<Data> list = new ImmutableList.Builder<>();

        for ( Data data : this.dataById.values() )
        {
            list.add( data );
        }
        return list.build();
    }

    public final Property getProperty( final String path )
    {
        return getProperty( DataPath.from( path ) );
    }

    public final Property getProperty( final DataId dataId )
    {
        final Data data = doGetData( dataId );
        if ( data == null )
        {
            return null;
        }
        Preconditions.checkArgument( data.isProperty(), "Data [%s] is not a Property: %s", dataId, data.getClass().getSimpleName() );
        return data.toProperty();
    }

    public final Property getProperty( final DataPath path )
    {
        final Data data = getData( path );
        if ( data == null )
        {
            return null;
        }
        Preconditions.checkArgument( data.isProperty(), "Data at path [%s] is not a Property: %s", path, data.getClass().getSimpleName() );
        return data.toProperty();
    }

    public final Property getProperty( final String name, final int arrayIndex )
    {
        DataPath.Element.checkName( name );

        final Data data = doGetData( DataId.from( name, arrayIndex ) );
        if ( data == null )
        {
            return null;
        }
        return data.toProperty();
    }

    /**
     * Returns a ImmutableList of found Property-s.
     */
    public ImmutableList<Property> getProperties()
    {
        final ImmutableList.Builder<Property> list = new ImmutableList.Builder<Property>();

        for ( Data data : this.dataById.values() )
        {
            if ( data.isProperty() )
            {
                list.add( data.toProperty() );
            }
        }
        return list.build();
    }

    /**
     * Returns all DataSet's with the given name.
     */
    public final List<Property> getPropertiesByName( final String name )
    {
        DataPath.Element.checkName( name );

        final DataArray array = arrayByDataName.get( name );

        if ( array == null )
        {
            return Lists.newArrayList();
        }
        else if ( array instanceof PropertyArray )
        {
            return ( (PropertyArray) array ).asList();
        }
        else
        {
            throw new IllegalArgumentException(
                "Data with name [" + name + "] in [" + getPath() + "] is not a Property: " + array.getClass().getSimpleName() );
        }
    }

    final Value getValue( final DataPath path )
    {
        final Data data = getData( path );
        if ( data == null )
        {
            return null;
        }

        Preconditions.checkArgument( data.isProperty(), "Data at path[%s] is not a Property: %s", path, data.getClass().getSimpleName() );
        final Property property = data.toProperty();
        if ( path.getLastElement().hasIndex() )
        {
            return property.getValue( path.getLastElement().getIndex() );
        }
        else
        {
            return property.getValue( 0 );
        }
    }

    final Value getValue( final String path )
    {
        return getValue( DataPath.from( path ) );
    }

    public final DataSet getDataSet( final String path )
    {
        final Data data = getData( DataPath.from( path ) );
        if ( data == null )
        {
            return null;
        }
        Preconditions.checkArgument( data.isDataSet(), "Data at path[%s] is not a DataSet: %s", path, data.getClass().getSimpleName() );
        return data.toDataSet();
    }

    public final DataSet getDataSet( final DataId dataId )
    {
        final Data data = doGetData( dataId );
        if ( data == null )
        {
            return null;
        }
        Preconditions.checkArgument( data.isProperty(), "Data [%s] is not a DataSet: %s", dataId, data.getClass().getSimpleName() );
        return data.toDataSet();
    }

    public final DataSet getDataSet( final DataPath path )
    {
        Preconditions.checkArgument( path.elementCount() > 0, "path must be something: " + path );

        if ( path.elementCount() == 1 )
        {
            final Data data = dataById.get( DataId.from( path.getLastElement() ) );
            if ( data == null )
            {
                return null;
            }
            return data.toDataSet();
        }
        else
        {
            final Data data = dataById.get( DataId.from( path.getFirstElement() ) );
            final DataSet dataSet = data.toDataSet();
            return dataSet.getDataSet( path.removeFirstPathElement() );
        }
    }

    public final DataSet getDataSet( final String name, final int arrayIndex )
    {
        DataPath.Element.checkName( name );

        final Data data = doGetData( DataId.from( name, arrayIndex ) );
        if ( data == null )
        {
            return null;
        }
        return data.toDataSet();
    }

    /**
     * Returns a ImmutableList of found DataSet-s.
     */
    public ImmutableList<DataSet> getDataSets()
    {
        final ImmutableList.Builder<DataSet> list = new ImmutableList.Builder<DataSet>();

        for ( Data data : this.dataById.values() )
        {
            if ( data.isDataSet() )
            {
                list.add( data.toDataSet() );
            }
        }
        return list.build();
    }

    /**
     * Returns all DataSet's with the given name.
     */
    public final List<DataSet> getDataSetsByName( final String name )
    {
        DataPath.Element.checkName( name );

        final DataArray array = arrayByDataName.get( name );

        if ( array == null )
        {
            return Lists.newArrayList();
        }
        else if ( array instanceof DataSetArray )
        {
            return ( (DataSetArray) array ).asList();
        }
        else
        {
            throw new IllegalArgumentException(
                "Data with name [" + name + "] in [" + getPath() + "] is not a DataSet: " + array.getClass().getSimpleName() );
        }
    }

    public final boolean isArray( final Data data )
    {
        final DataArray dataArray = arrayByDataName.get( data.getName() );
        return dataArray.size() > 1;
    }

    public final DataArray getArray( final Data data )
    {
        return arrayByDataName.get( data.getName() );
    }

    @Override
    public final DataSetArray getArray()
    {
        return (DataSetArray) super.getArray();
    }

    public RootDataSet toRootDataSet()
    {
        if ( this instanceof RootDataSet )
        {
            return (RootDataSet) this;
        }
        return new RootDataSet( newDataSet( this ) );
    }

    @Override
    public DataSet copy()
    {
        return newDataSet( this ).build();
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

        final DataSet other = (DataSet) o;

        return super.equals( o ) && Objects.equals( dataById, other.dataById );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), dataById );
    }

    public boolean valueEquals( final Data data )
    {
        final DataSet other = data.toDataSet();

        if ( this.dataById.size() != other.dataById.size() )
        {
            return false;
        }

        for ( Map.Entry<DataId, Data> thisEntry : dataById.entrySet() )
        {
            final Data otherValue = other.dataById.get( thisEntry.getKey() );
            if ( otherValue == null )
            {
                return false;
            }
            if ( !otherValue.valueEquals( thisEntry.getValue() ) )
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, Object> toMap()
    {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<>( this.dataById.size() );
        for ( Map.Entry<DataId, Data> entry : this.dataById.entrySet() )
        {
            final String name = entry.getKey().getName();
            final Data data = entry.getValue();
            if ( data instanceof Property )
            {
                final Property property = (Property) data;
                final Object propertyValue = property.getValue().getObject();
                final Object mapValue = propertyValue instanceof DataSet ? ( (DataSet) propertyValue ).toMap() : propertyValue;
                setMapValue( map, name, mapValue );
            }
            else if ( data instanceof DataSet )
            {
                final DataSet dataSet = (DataSet) data;
                setMapValue( map, name, dataSet.toMap() );
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
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        if ( !StringUtils.isEmpty( getName() ) )
        {
            s.append( getName() );
        }
        if ( getArrayIndex() > -1 )
        {
            s.append( "[" ).append( getArrayIndex() ).append( "]" );
        }
        if ( s.length() > 0 )
        {
            s.append( " " );
        }
        s.append( "{ " );
        int index = 0;
        final int size = size();
        for ( Data data : this )
        {
            s.append( data.getDataId() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        s.append( " }" );
        return s.toString();
    }

    public static class Builder
    {
        private String name;

        private List<Data> dataList = new ArrayList<>();

        public Builder()
        {
        }

        public Builder( final DataSet dataSet )
        {
            this.name = dataSet.getName();
            for ( final Data data : dataSet )
            {
                this.dataList.add( data.copy() );
            }
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder set( final String name, final Object value, final ValueType propertyType )
        {
            final Value valueObject = Value.newValue( propertyType, value );
            dataList.add( Property.newProperty( name, valueObject ) );
            return this;
        }

        public Builder data( final Iterable<Data> datas )
        {
            for ( Data data : datas )
            {
                dataList.add( data );
            }
            return this;
        }


        public DataSet build()
        {
            return new DataSet( this );
        }
    }
}
