package com.enonic.xp.xml.parser;

import java.util.List;

import org.w3c.dom.Element;

import com.google.common.collect.Lists;

import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.xml.DomHelper;

public final class XmlContentTypeParser
    extends XmlModelParser<XmlContentTypeParser>
{
    private ContentType.Builder builder;

    private ModuleRelativeResolver resolver;

    public XmlContentTypeParser builder( final ContentType.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final Element root )
        throws Exception
    {
        this.resolver = new ModuleRelativeResolver( this.currentModule );

        assertTagName( root, "content-type" );
        this.builder.displayName( DomHelper.getChildElementValueByTagName( root, "display-name" ) );
        this.builder.description( DomHelper.getChildElementValueByTagName( root, "description" ) );

        this.builder.contentDisplayNameScript( DomHelper.getChildElementValueByTagName( root, "content-display-name-script" ) );
        this.builder.superType( this.resolver.toContentTypeName( DomHelper.getChildElementValueByTagName( root, "super-type" ) ) );

        this.builder.setAbstract( XmlParserHelper.getChildElementAsBoolean( root, "is-abstract", false ) );
        this.builder.setFinal( XmlParserHelper.getChildElementAsBoolean( root, "is-final", false ) );
        this.builder.allowChildContent( XmlParserHelper.getChildElementAsBoolean( root, "allow-child-content", false ) );

        this.builder.metadata( buildMetaData( root ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentModule );
        this.builder.form( mapper.buildForm( DomHelper.getChildElementByTagName( root, "form" ) ) );
    }

    private MixinNames buildMetaData( final Element root )
    {
        final List<MixinName> names = Lists.newArrayList();
        for ( final Element child : DomHelper.getChildElementsByTagName( root, "x-data" ) )
        {
            final String name = XmlParserHelper.getAttributeAsString( child, "mixin", null );
            names.add( this.resolver.toMixinName( name ) );
        }

        return MixinNames.from( names );
    }
}
