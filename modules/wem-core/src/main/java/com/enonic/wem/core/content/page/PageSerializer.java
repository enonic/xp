package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.region.PageRegions;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

public class PageSerializer
{
    public static final String PAGE_TEMPLATE = "template";

    public static final String PAGE_REGIONS = "regions";

    public static final String PAGE_CONFIG = "config";

    public DataSet toData( final Page page, final String dataSetName )
    {
        if ( page == null )
        {
            return null;
        }

        final DataSet dataSet = new DataSet( dataSetName );

        dataSet.addProperty( PAGE_TEMPLATE, new Value.String( page.getTemplate().toString() ) );

        final DataSet regionsDataSet = new DataSet( PAGE_REGIONS );
        if ( page.getRegions() != null )
        {
            page.getRegions().toData( regionsDataSet );
        }
        dataSet.add( regionsDataSet );

        dataSet.add( page.getConfig().toDataSet( PAGE_CONFIG ) );

        return dataSet;
    }


    public Page toPage( final DataSet dataSet )
    {
        final Page.Builder page = Page.newPage().
            config( dataSet.getData( PAGE_CONFIG ).toDataSet().toRootDataSet() ).
            template( PageTemplateKey.from( dataSet.getProperty( PAGE_TEMPLATE ).getString() ) );

        final DataSet regionsDataSet = dataSet.getDataSet( PAGE_REGIONS );
        PageRegions regions = PageRegions.from( regionsDataSet );
        page.regions( regions );

        return page.build();
    }

}
