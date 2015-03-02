package com.enonic.xp.xml.parser;

import org.w3c.dom.Element;

import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.xml.DomHelper;

public final class XmlPageDescriptorParser
    extends XmlModelParser<XmlPageDescriptorParser>
{
    private PageDescriptor.Builder builder;

    public XmlPageDescriptorParser builder( final PageDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final Element root )
        throws Exception
    {
        assertTagName( root, "page-component" );
        this.builder.displayName( DomHelper.getChildElementValueByTagName( root, "display-name" ) );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentModule );
        this.builder.config( formMapper.buildForm( DomHelper.getChildElementByTagName( root, "config" ) ) );

        final XmlRegionDescriptorsMapper regionsMapper = new XmlRegionDescriptorsMapper();
        this.builder.regions( regionsMapper.buildRegions( DomHelper.getChildElementByTagName( root, "regions" ) ) );
    }
}
