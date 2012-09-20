package com.enonic.wem.core.content.data;


import java.util.Iterator;

import org.jdom.Element;

public class ContentDataSerializerXml
{
    private DataSerializerXml dataSerializer = new DataSerializerXml();

    public Element generate( ContentData contentData )
    {
        Element dataEl = new Element( "data" );
        for ( final Data data : contentData )
        {
            dataEl.addContent( dataSerializer.generate( data ) );
        }
        return dataEl;
    }

    public ContentData parse( final Element contentDataEl )
    {

        ContentData contentData = new ContentData();
        DataSet dataSet = new DataSet( new EntryPath( "" ) );
        final Iterator<Element> dataIt = contentDataEl.getChildren().iterator();
        while ( dataIt.hasNext() )
        {
            final Element dataEl = dataIt.next();
            dataSet.add( dataSerializer.parse( new EntryPath(), dataEl ) );
        }
        contentData.setDataSet( dataSet );

        return contentData;
    }
}
