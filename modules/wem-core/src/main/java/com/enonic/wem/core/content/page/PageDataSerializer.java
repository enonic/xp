package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public class PageDataSerializer
    extends AbstractDataSetSerializer<Page, Page>
{
    public static final String PAGE_TEMPLATE = "template";

    public static final String PAGE_REGIONS = "regions";

    public static final String PAGE_CONFIG = "config";

    private static PageRegionsDataSerializer regionsDataSerializer = new PageRegionsDataSerializer( PAGE_REGIONS );

    private final String dataSetName;

    public PageDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    public DataSet toData( final Page page )
    {
        final DataSet dataSet = new DataSet( dataSetName );

        dataSet.addProperty( PAGE_TEMPLATE, new Value.String( page.getTemplate().toString() ) );
        if ( page.hasRegions() )
        {
            dataSet.add( regionsDataSerializer.toData( page.getRegions() ) );
        }
        if ( page.hasConfig() )
        {
            dataSet.add( page.getConfig().toDataSet( PAGE_CONFIG ) );
        }

        return dataSet;
    }


    public Page fromData( final DataSet asData )
    {
        final Page.Builder page = Page.newPage();
        page.template( PageTemplateKey.from( asData.getProperty( PAGE_TEMPLATE ).getString() ) );
        if ( asData.hasData( PAGE_REGIONS ) )
        {
            page.regions( regionsDataSerializer.fromData( asData.getDataSet( PAGE_REGIONS ) ) );
        }
        if ( asData.hasData( PAGE_CONFIG ) )
        {
            page.config( asData.getData( PAGE_CONFIG ).toDataSet().toRootDataSet() );
        }
        return page.build();
    }

}
