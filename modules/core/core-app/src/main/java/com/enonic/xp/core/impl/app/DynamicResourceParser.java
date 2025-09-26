package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlPartDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlContentTypeParser;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;
import com.enonic.xp.xml.parser.XmlMixinParser;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;
import com.enonic.xp.xml.parser.XmlSiteParser;
import com.enonic.xp.xml.parser.XmlStyleDescriptorParser;
import com.enonic.xp.xml.parser.XmlXDataParser;

final class DynamicResourceParser
{
    private final ContentTypeService contentTypeService;

    DynamicResourceParser( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    ComponentDescriptor parseComponent( final DescriptorKey key, final DynamicComponentType type, final String resource )
    {
        switch ( type )
        {
            case PAGE:
                return parsePageDescriptor( key, resource );
            case PART:
                return parsePartDescriptor( key, resource );
            case LAYOUT:
                return parseLayoutDescriptor( key, resource );
            default:
                throw new IllegalArgumentException( String.format( "unknown dynamic component type: '%s'", type ) );
        }
    }

    BaseSchema<?> parseSchema( final BaseSchemaName name, final DynamicContentSchemaType type, final String resource )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return parseContentTypeDescriptor( (ContentTypeName) name, resource );
            case MIXIN:
                return parseMixinDescriptor( (MixinName) name, resource );
            case XDATA:
                return parseXDataDescriptor( (XDataName) name, resource );
            default:
                throw new IllegalArgumentException( String.format( "unknown dynamic schema type: '%s'", type ) );
        }
    }

    SiteDescriptor parseSite( final ApplicationKey applicationKey, final String resource )
    {
        return parseSiteDescriptor( applicationKey, resource );
    }

    StyleDescriptor parseStyles( final ApplicationKey applicationKey, final String resource )
    {
        return parseStylesDescriptor( applicationKey, resource );
    }


    private PageDescriptor parsePageDescriptor( final DescriptorKey key, final String resource )
    {
        final PageDescriptor.Builder builder = PageDescriptor.create().key( key );
        try
        {
            final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( key.getApplicationKey() );
            parser.source( resource );
            builder.key( key );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic page descriptor [" + key + "]" );
        }
        return builder.build();
    }

    private PartDescriptor parsePartDescriptor( final DescriptorKey key, final String resource )
    {
        final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( resource, key.getApplicationKey() );
        builder.key( key );
        return builder.build();
    }

    private LayoutDescriptor parseLayoutDescriptor( final DescriptorKey key, final String resource )
    {
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();
        builder.key( key );
        try
        {
            final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( key.getApplicationKey() );
            parser.source( resource );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic layout descriptor [" + key + "]" );
        }
        return builder.build();
    }

    private ContentType parseContentTypeDescriptor( final ContentTypeName name, final String resource )
    {
        // TODO: Remove later, kept just for testing
        if ( "xml".equals( name.getExtension() ) )
        {
            try
            {
                final ContentType.Builder builder = ContentType.create();
                final XmlContentTypeParser parser = new XmlContentTypeParser();
                parser.currentApplication( name.getApplicationKey() );
                parser.source( resource );
                parser.builder( builder );
                parser.parse();
                builder.name( name );

                return builder.build();
            }
            catch ( Exception e )
            {
                throw new XmlException( e, "Could not parse dynamic content type [" + name + "]" );
            }
        }
        else
        {
            try
            {
                final ContentType.Builder builder = contentTypeService.createContentTypeFromYml( resource, name.getApplicationKey() );
                builder.name( name );
                return builder.build();
            }
            catch ( Exception e )
            {
                throw new RuntimeException( String.format( "Could not parse dynamic content type [%s]", name ), e );
            }
        }
    }

    private Mixin parseMixinDescriptor( final MixinName name, final String resource )
    {
        final Mixin.Builder builder = Mixin.create();
        try
        {
            final XmlMixinParser parser = new XmlMixinParser();
            parser.currentApplication( name.getApplicationKey() );
            parser.source( resource );
            parser.builder( builder );
            parser.parse();
        }
        catch ( Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic mixin [" + name + "]" );
        }

        builder.name( name );

        return builder.build();
    }

    private XData parseXDataDescriptor( final XDataName name, final String resource )
    {
        final XData.Builder builder = XData.create();
        try
        {
            final XmlXDataParser parser = new XmlXDataParser();
            parser.currentApplication( name.getApplicationKey() );
            parser.source( resource );
            parser.builder( builder );
            parser.parse();
        }
        catch ( Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic xdata [" + name + "]" );
        }

        builder.name( name );

        return builder.build();
    }

    private SiteDescriptor parseSiteDescriptor( final ApplicationKey applicationKey, final String resource )
    {
        final SiteDescriptor.Builder builder = SiteDescriptor.create();
        try
        {
            final XmlSiteParser parser = new XmlSiteParser();
            parser.currentApplication( applicationKey );
            parser.source( resource );
            parser.siteDescriptorBuilder( builder );
            parser.parse();

            builder.applicationKey( applicationKey );
        }
        catch ( Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic site descriptor, application key: [" + applicationKey + "]" );
        }
        return builder.build();
    }

    private StyleDescriptor parseStylesDescriptor( final ApplicationKey applicationKey, final String resource )
    {
        final StyleDescriptor.Builder builder = StyleDescriptor.create();
        builder.application( applicationKey );
        try
        {
            final XmlStyleDescriptorParser parser = new XmlStyleDescriptorParser();
            parser.currentApplication( applicationKey );
            parser.source( resource );
            parser.styleDescriptorBuilder( builder );
            parser.parse();
        }
        catch ( Exception e )
        {
            throw new XmlException( e, "Could not parse dynamic style descriptor, application key: [" + applicationKey + "]" );
        }
        return builder.build();
    }
}
