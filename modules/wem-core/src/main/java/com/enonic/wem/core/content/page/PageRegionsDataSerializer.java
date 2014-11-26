package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionDataSerializer;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

public class PageRegionsDataSerializer
    extends AbstractDataSetSerializer<PageRegions, PageRegions>
{
    private final String dataSetName;

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    public PageRegionsDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    public DataSet toData( final PageRegions regions )
    {
        final DataSet asData = new DataSet( dataSetName );

        for ( Region region : regions )
        {
            asData.add( regionDataSerializer.toData( region ) );
        }

        return asData;
    }


    public PageRegions fromData( final DataSet asData )
    {
        final PageRegions.Builder regions = newPageRegions();

        for ( DataSet regionAsData : asData.getDataSets() )
        {
            regions.add( regionDataSerializer.fromData( regionAsData ) );
        }

        return regions.build();
    }

}
