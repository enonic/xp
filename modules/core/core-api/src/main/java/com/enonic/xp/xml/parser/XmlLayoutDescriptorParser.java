package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.region.LayoutDescriptor;
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
        assertTagName( root, "layout" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( formMapper.buildForm( root.getChild( "config" ) ) );

        final XmlRegionDescriptorsMapper regionsMapper = new XmlRegionDescriptorsMapper();
        this.builder.regions( regionsMapper.buildRegions( root.getChild( "regions" ) ) );
    }
}
