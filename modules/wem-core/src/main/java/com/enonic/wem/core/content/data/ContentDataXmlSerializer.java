package com.enonic.wem.core.content.data;


import org.jdom.Element;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Entry;

public final class ContentDataXmlSerializer
{
    private EntryXmlSerializer dataSerializer = new EntryXmlSerializer();

    public Element generate( ContentData contentData )
    {
        Element dataEl = new Element( "data" );
        for ( final Entry entry : contentData )
        {
            dataSerializer.generate( dataEl, entry );
        }
        return dataEl;
    }

    public ContentData parse( final Element contentDataEl )
    {
        final ContentData contentData = new ContentData();
        dataSerializer.parse( contentDataEl, contentData.getDataSet() );
        return contentData;
    }
}
