package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
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
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "part" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( mapper.buildForm( root.getChild( "config" ) ) );
    }
}
