package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.core.content.page.region.RegionDataSerializer;

import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;

public class LayoutRegionsDataSerializer
    extends AbstractDataSetSerializer<LayoutRegions, LayoutRegions>
{
    private final String dataSetName;

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    public LayoutRegionsDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    public DataSet toData( final LayoutRegions regions )
    {
        final DataSet asData = new DataSet( dataSetName );

        for ( Region region : regions )
        {
            asData.add( regionDataSerializer.toData( region ) );
        }

        return asData;
    }


    public LayoutRegions fromData( final DataSet asData )
    {
        final LayoutRegions.Builder regions = newLayoutRegions();

        for ( DataSet regionAsData : asData.getDataSets() )
        {
            regions.add( regionDataSerializer.fromData( regionAsData ) );
        }

        return regions.build();
    }

}
