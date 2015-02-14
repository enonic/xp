package com.enonic.wem.api.xml.mapper;

import java.util.List;

import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.xml.model.XmlRegionDescriptor;
import com.enonic.wem.api.xml.model.XmlRegions;

final class XmlRegionDescriptorMapper
{
    private final ModuleKey currentModule;

    public XmlRegionDescriptorMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    protected RegionDescriptors fromXml( final XmlRegions regions )
    {
        final RegionDescriptors.Builder builder = RegionDescriptors.newRegionDescriptors();

        if ( regions != null )
        {
            fromXml( builder, regions.getRegion() );
        }

        return builder.build();
    }

    private void fromXml( final RegionDescriptors.Builder builder, final List<XmlRegionDescriptor> regions )
    {
        if ( regions != null )
        {
            for ( final XmlRegionDescriptor descriptor : regions )
            {
                builder.add( fromXml( descriptor ) );
            }
        }
    }

    protected RegionDescriptor fromXml( final XmlRegionDescriptor descriptor )
    {
        final RegionDescriptor.Builder builder = RegionDescriptor.newRegionDescriptor();
        builder.name( descriptor.getName() );
        return builder.build();
    }

    protected XmlRegions toXml( final RegionDescriptors object )
    {
        final XmlRegions result = new XmlRegions();
        for ( final RegionDescriptor descriptor : object )
        {
            result.getRegion().add( toXml( descriptor ) );
        }
        return result;
    }

    protected XmlRegionDescriptor toXml( final RegionDescriptor object )
    {
        final XmlRegionDescriptor result = new XmlRegionDescriptor();
        result.setName( object.getName() );
        return result;
    }
}
