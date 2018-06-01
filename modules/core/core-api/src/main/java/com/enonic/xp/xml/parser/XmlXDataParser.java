package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlXDataParser
    extends XmlModelParser<XmlXDataParser>
{
    private XData.Builder builder;

    public XmlXDataParser builder( final XData.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "x-data" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        final Form form = mapper.buildForm( root.getChild( "items" ) );
        this.builder.form( form );

        final ApplicationRelativeResolver applicationRelativeResolver =
            this.currentApplication != null ? new ApplicationRelativeResolver( this.currentApplication ) : null;

        root.getChildren( "allowContentType" ).forEach( domElement -> {

            final String value = applicationRelativeResolver != null
                ? applicationRelativeResolver.toContentTypeNameRegexp( domElement.getValue() )
                : domElement.getValue();

            this.builder.allowContentType( value );
        } );
    }
}
