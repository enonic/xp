package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.json.content.page.DescriptorJson;
import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.content.page.region.RegionDescriptor;
import com.enonic.xp.content.page.region.RegionDescriptors;


public class LayoutDescriptorJson
    extends DescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    private final List<RegionDescriptorJson> regionsJson;

    public LayoutDescriptorJson( final LayoutDescriptor descriptor )
    {
        super( descriptor );
        this.editable = false;
        this.deletable = false;

        final RegionDescriptors regions = descriptor.getRegions();
        this.regionsJson = new ArrayList<>( regions.numberOfRegions() );
        for ( final RegionDescriptor regionDescriptor : regions )
        {
            this.regionsJson.add( new RegionDescriptorJson( regionDescriptor ) );
        }
    }

    public List<RegionDescriptorJson> getRegions()
    {
        return this.regionsJson;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
