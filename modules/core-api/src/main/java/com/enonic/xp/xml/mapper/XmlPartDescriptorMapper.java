package com.enonic.xp.xml.mapper;


import com.enonic.xp.content.page.region.PartDescriptor;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.xml.model.XmlPartDescriptor;

public final class XmlPartDescriptorMapper
{
    private final ModuleKey currentModule;

    public XmlPartDescriptorMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlPartDescriptor toXml( final PartDescriptor object )
    {
        final XmlPartDescriptor result = new XmlPartDescriptor();
        result.setDisplayName( object.getDisplayName() );
        result.setConfig( new XmlFormMapper( currentModule ).toXml( object.getConfig() ) );
        return result;
    }

    public void fromXml( final XmlPartDescriptor xml, final PartDescriptor.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.config( new XmlFormMapper( currentModule ).fromXml( xml.getConfig() ) );
    }

}
