package com.enonic.wem.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.xml.XmlObject;

@XmlJavaTypeAdapter(DataAdapter.class)
public class DataXml
    implements XmlObject<Data, DataSet.Builder>
{
    private String name;

    private String type;

    private String value;

    private List<DataXml> dataItems = new ArrayList<>();

    String getName()
    {
        return name;
    }

    void setName( String name )
    {
        this.name = name;
    }

    String getType()
    {
        return type;
    }

    void setType( final String type )
    {
        this.type = type;
    }

    String getValue()
    {
        return value;
    }

    void setValue( final String value )
    {
        this.value = value;
    }

    boolean isDataSet()
    {
        return dataItems != null && !dataItems.isEmpty();
    }

    @XmlAnyElement
    List<DataXml> getItems()
    {
        return dataItems;
    }

    void setItems( List<DataXml> items )
    {
        this.dataItems = items;
    }

    @Override
    public void from( final Data data )
    {
        setName( data.getName() );
        if ( data.isProperty() )
        {
            final Property property = data.toProperty();
            setType( property.getValueType().getName() );
            setValue( property.getValue().asString() );
        }
        else if ( data.isDataSet() )
        {
            final DataSet dataSet = data.toDataSet();
            for ( Data dataSetItem : dataSet )
            {
                final DataXml dataItem = new DataXml();
                dataItem.from( dataSetItem );
                this.dataItems.add( dataItem );
            }
        }
    }

    @Override
    public void to( final DataSet.Builder output )
    {
        if ( this.isDataSet() )
        {
            final List<Data> datas = Lists.newArrayList();
            for ( DataXml dataXml : this.dataItems )
            {
                DataSet.Builder builder = DataSet.newDataSet();
                dataXml.to( builder );
                datas.add( builder.build() );
            }
            output.data( datas );
        }
        else
        {
            final ValueType valueType = ValueTypes.parseByName( this.getType() );
            output.set( this.getName(), this.getValue(), valueType );
        }
    }
}
