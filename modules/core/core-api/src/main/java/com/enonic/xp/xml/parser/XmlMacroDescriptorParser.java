package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlMacroDescriptorParser
    extends XmlModelParser<XmlContentTypeParser>
{
    private MacroDescriptor.Builder builder;

    public XmlModelParser builder( final MacroDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "macro" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );


        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.form( mapper.buildForm( root.getChild( "form" ) ) );
    }

}
