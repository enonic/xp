package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

public class PageSerializer
{
    static final String PAGE_TEMPLATE = "template";

    static final String PAGE_CONFIG = "config";

    public DataSet toData( final Page page, final String dataSetName )
    {
        if ( page == null )
        {
            return null;
        }

        final DataSet dataSet = new DataSet( dataSetName );
        dataSet.addProperty( PAGE_TEMPLATE, new Value.String( page.getTemplate().toString() ) );
        dataSet.add( page.getConfig().toDataSet( PAGE_CONFIG ) );

        return dataSet;
    }


    public Page toPage( final DataSet dataSet )
    {
        final Page page = Page.newPage().
            config( dataSet.getData( PAGE_CONFIG ).toDataSet().toRootDataSet() ).
            template( PageTemplateKey.from( dataSet.getProperty( PAGE_TEMPLATE ).getString() ) ).
            build();

        return page;
    }

}
