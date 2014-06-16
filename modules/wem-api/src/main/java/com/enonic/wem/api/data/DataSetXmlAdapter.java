package com.enonic.wem.api.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jdom2.input.DOMBuilder;
import org.jdom2.output.DOMOutputter;
import org.w3c.dom.Element;

import com.enonic.wem.api.data.serializer.DataXmlSerializer;

public class DataSetXmlAdapter
    extends XmlAdapter<Object, DataSetXml>
{
    @Override
    public DataSetXml unmarshal( final Object object )
        throws Exception
    {
        final Element element = Element.class.cast( object );
        final org.jdom2.Element dataEl = new DOMBuilder().build( element );

        final DataSet dataSet = new DataXmlSerializer().parse( dataEl );
        return new DataSetXml( dataSet );
    }

    @Override
    public Object marshal( final DataSetXml dataSetXml )
        throws Exception
    {
        if ( dataSetXml != null && dataSetXml.getDataSet() != null )
        {
            final org.jdom2.Element dataEl = new org.jdom2.Element( "data-set-parent" );
            new DataXmlSerializer().generateRootDataSet( dataEl, dataSetXml.getDataSet().toRootDataSet() );
            return new DOMOutputter().output( dataEl );
        }

        return null;
    }
}
