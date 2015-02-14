package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.content.page.region.LayoutDescriptor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;

public final class XmlLayoutDescriptorMapper
{
    private final ModuleKey currentModule;

    public XmlLayoutDescriptorMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlLayoutDescriptor toXml( final LayoutDescriptor object )
    {
        final XmlLayoutDescriptor result = new XmlLayoutDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( new XmlFormMapper( currentModule ).toXml( object.getConfig() ) );
        result.setRegions( new XmlRegionDescriptorMapper( currentModule ).toXml( object.getRegions() ) );
        return result;
    }

    public void fromXml( final XmlLayoutDescriptor xml, final LayoutDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( new XmlFormMapper( currentModule ).fromXml( xml.getConfig() ) );
        builder.regions( new XmlRegionDescriptorMapper( currentModule ).fromXml( xml.getRegions() ) );
    }

}
