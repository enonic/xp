package com.enonic.wem.admin.json.content.page;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.admin.json.content.page.region.RegionDescriptorJson;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;

public class PageDescriptorJson
    extends DescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    private final List<RegionDescriptorJson> regionsJson;

    public PageDescriptorJson( final PageDescriptor descriptor )
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
