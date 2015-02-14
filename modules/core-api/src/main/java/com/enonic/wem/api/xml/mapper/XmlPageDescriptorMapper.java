package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;

public final class XmlPageDescriptorMapper
{
    private final ModuleKey currentModule;

    public XmlPageDescriptorMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlPageDescriptor toXml( final PageDescriptor object )
    {
        final XmlPageDescriptor result = new XmlPageDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( new XmlFormMapper( currentModule ).toXml( object.getConfig() ) );
        result.setRegions( new XmlRegionDescriptorMapper( currentModule ).toXml( object.getRegions() ) );
        return result;
    }

    public void fromXml( final XmlPageDescriptor xml, final PageDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( new XmlFormMapper( currentModule ).fromXml( xml.getConfig() ) );
        builder.regions( new XmlRegionDescriptorMapper( currentModule ).fromXml( xml.getRegions() ) );
    }
}
