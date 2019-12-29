package com.enonic.xp.xml.parser;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.xml.DomElement;

@PublicApi
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

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( formMapper.buildForm( root.getChild( "form" ) ) );

        final XmlRegionDescriptorsMapper regionsMapper = new XmlRegionDescriptorsMapper();
        this.builder.regions( regionsMapper.buildRegions( root.getChild( "regions" ) ) );
    }
}
