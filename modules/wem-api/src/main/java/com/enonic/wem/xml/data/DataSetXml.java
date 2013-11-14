package com.enonic.wem.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

import com.google.common.collect.Lists;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.xml.XmlObject;

public class DataSetXml
    implements XmlObject<DataSet, DataSet.Builder>
{
    private List<DataXml> dataItems = new ArrayList<>();

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
    public void from( final DataSet dataSet )
    {
        for ( Data data : dataSet )
        {
            DataXml dataXml = new DataXml();
            dataXml.from( data );
            this.dataItems.add( dataXml );
        }
    }

    @Override
    public void to( final DataSet.Builder output )
    {
        final List<Data> datas = Lists.newArrayList();
        for ( DataXml dataXml : this.dataItems )
        {
            dataXml.to( output );
        }
        output.data( datas );
    }
}
