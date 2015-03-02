package com.enonic.xp.xml.parser;

import org.w3c.dom.Element;

import com.enonic.xp.content.page.region.PartDescriptor;
import com.enonic.xp.xml.DomHelper;

public final class XmlPartDescriptorParser
    extends XmlModelParser<XmlPartDescriptorParser>
{
    private PartDescriptor.Builder builder;

    public XmlPartDescriptorParser builder( final PartDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final Element root )
        throws Exception
    {
        assertTagName( root, "part-component" );
        this.builder.displayName( DomHelper.getChildElementValueByTagName( root, "display-name" ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentModule );
        this.builder.config( mapper.buildForm( DomHelper.getChildElementByTagName( root, "config" ) ) );
    }
}
