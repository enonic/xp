package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlLayoutDescriptorParser
    extends XmlModelParser<XmlLayoutDescriptorParser>
{
    private LayoutDescriptor.Builder builder;

    public XmlLayoutDescriptorParser builder( final LayoutDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "layout-component" );
        this.builder.displayName( root.getChildValue( "display-name" ) );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentModule );
        this.builder.config( formMapper.buildForm( root.getChild( "config" ) ) );

        final XmlRegionDescriptorsMapper regionsMapper = new XmlRegionDescriptorsMapper();
        this.builder.regions( regionsMapper.buildRegions( root.getChild( "regions" ) ) );
    }
}
