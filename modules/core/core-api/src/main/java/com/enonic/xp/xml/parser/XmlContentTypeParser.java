package com.enonic.xp.xml.parser;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.xml.DomElement;

@Beta
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

        this.builder.contentDisplayNameScript( root.getChildValue( "content-display-name-script" ) );
        this.builder.superType( this.resolver.toContentTypeName( root.getChildValue( "super-type" ) ) );

        this.builder.setAbstract( root.getChildValueAs( "is-abstract", Boolean.class, false ) );
        this.builder.setFinal( root.getChildValueAs( "is-final", Boolean.class, false ) );
        this.builder.allowChildContent( root.getChildValueAs( "allow-child-content", Boolean.class, true ) );

        this.builder.metadata( buildMetaData( root ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.form( mapper.buildForm( root.getChild( "form" ) ) );
    }

    private MixinNames buildMetaData( final DomElement root )
    {
        final List<MixinName> names = Lists.newArrayList();
        for ( final DomElement child : root.getChildren( "x-data" ) )
        {
            String name = child.getAttribute( "name" );
            if ( StringUtils.isEmpty( name ) )
            {
                name = child.getAttribute( "mixin" );
            }
            names.add( this.resolver.toMixinName( name ) );
        }

        return MixinNames.from( names );
    }
}
