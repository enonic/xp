package com.enonic.wem.api.data;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.xml.XmlObject;

@XmlRootElement
public class DataSetXml2
    implements XmlObject<DataSet, DataSet>
{
    private DataSet dataSet;

    public DataSetXml2()
    {
        this.dataSet = null;
    }

    DataSetXml2( final DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    DataSet getDataSet()
    {
        return dataSet;
    }

    @Override
    public void from( final DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    @Override
    public void to( final DataSet output )
    {
        if ( this.dataSet != null )
        {
            for ( Data data : this.dataSet )
            {
                output.add( data.copy() );
            }
        }
    }
}
