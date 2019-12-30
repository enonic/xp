package com.enonic.xp.xml.parser;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.xml.DomElement;

@PublicApi
public final class XmlContentTypeParser
    extends XmlModelParser<XmlContentTypeParser>
{
    private ContentType.Builder builder;

    private ApplicationRelativeResolver resolver;

    public XmlContentTypeParser builder( final ContentType.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        this.resolver = new ApplicationRelativeResolver( this.currentApplication );

        assertTagName( root, "content-type" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );
        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );

        this.builder.displayNameExpression( root.getChildValue( "display-name-expression" ) );
        this.builder.superType( this.resolver.toContentTypeName( root.getChildValue( "super-type" ) ) );

        this.builder.setAbstract( root.getChildValueAs( "is-abstract", Boolean.class, false ) );
        this.builder.setFinal( root.getChildValueAs( "is-final", Boolean.class, false ) );
        this.builder.allowChildContent( root.getChildValueAs( "allow-child-content", Boolean.class, true ) );

        this.builder.xData( buildMetaData( root ) );

        this.builder.displayNameLabel( root.getChildValue( "display-name-label" ) );
        this.builder.displayNameLabelI18nKey(
            root.getChild( "display-name-label" ) != null ? root.getChild( "display-name-label" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.form( mapper.buildForm( root.getChild( "form" ) ) );
    }

    private XDataNames buildMetaData( final DomElement root )
    {
        final List<XDataName> names = new ArrayList<>();
        for ( final DomElement child : root.getChildren( "x-data" ) )
        {
            String name = child.getAttribute( "name" );
            names.add( this.resolver.toXDataName( name ) );
        }
        return XDataNames.from( names );
    }
}
