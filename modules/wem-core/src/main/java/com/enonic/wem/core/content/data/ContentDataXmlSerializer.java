package com.enonic.wem.core.content.data;


import org.jdom.Element;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;

public final class ContentDataXmlSerializer
{
    private DataXmlSerializer dataSerializer = new DataXmlSerializer();

    public Element generate( ContentData contentData )
    {
        Element dataEl = new Element( "data" );
        for ( final Data data : contentData )
        {
            dataSerializer.generate( dataEl, data );
        }
        return dataEl;
    }

    public ContentData parse( final Element contentDataEl )
    {
        final ContentData contentData = new ContentData();
        final DataSet dataSet = new DataSet( new EntryPath() );
        dataSerializer.parse( contentDataEl, dataSet );
        contentData.setDataSet( dataSet );

        return contentData;
    }
}
