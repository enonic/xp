package com.enonic.wem.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "dataSet")
public class DataSetXml
    implements XmlObject<DataSet, DataSet>, DataXml
{

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElements({@XmlElement(name = "dataSet", type = DataSetXml.class), @XmlElement(name = "property", type = PropertyXml.class)})
    private List<DataXml> dataItems = new ArrayList<>();

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void from( final DataSet dataSet )
    {
        for ( Data data : dataSet )
        {
            if ( data.isProperty() )
            {
                final PropertyXml propertyXml = new PropertyXml();
                propertyXml.from( data.toProperty() );
                this.dataItems.add( propertyXml );
            }
            else if ( data.isDataSet() )
            {
                final DataSetXml dataSetXml = new DataSetXml();
                dataSetXml.name = data.getName();
                dataSetXml.from( data.toDataSet() );
                this.dataItems.add( dataSetXml );
            }
        }
    }

    @Override
    public void to( final DataSet output )
    {
        for ( DataXml dataXml : this.dataItems )
        {
            if ( dataXml instanceof PropertyXml )
            {
                ( (PropertyXml) dataXml ).to( output );
            }
            else if ( dataXml instanceof DataSetXml )
            {
                final DataSet dataSet = new DataSet( dataXml.getName() );
                final DataSetXml dataSetXml = (DataSetXml) dataXml;
                dataSetXml.to( dataSet );
                output.add( dataSet );
            }
        }
    }
}
