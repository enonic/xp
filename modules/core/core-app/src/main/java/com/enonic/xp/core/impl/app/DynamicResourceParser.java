package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlCmsDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.content.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPageDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPartDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlStyleDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlXDataParser;
import com.enonic.xp.core.impl.form.mixin.YmlFormFragmentParser;
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
import com.enonic.xp.schema.mixin.FormFragmentDescriptor;
import com.enonic.xp.schema.mixin.FormFragmentName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

final class DynamicResourceParser
{
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
            case FORM_FRAGMENT:
                return parseFormFragmentDescriptor( (FormFragmentName) name, resource );
            case XDATA:
                return parseXDataDescriptor( (XDataName) name, resource );
            default:
                throw new IllegalArgumentException( String.format( "unknown dynamic schema type: '%s'", type ) );
        }
    }

    CmsDescriptor parseCms( final ApplicationKey applicationKey, final String resource )
    {
        return parseCmsDescriptor( applicationKey, resource );
    }

    StyleDescriptor parseStyles( final ApplicationKey applicationKey, final String resource )
    {
        return parseStylesDescriptor( applicationKey, resource );
    }


    private PageDescriptor parsePageDescriptor( final DescriptorKey key, final String resource )
    {
        return YmlPageDescriptorParser.parse( resource, key.getApplicationKey() ).key( key ).build();
    }

    private PartDescriptor parsePartDescriptor( final DescriptorKey key, final String resource )
    {
        final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( resource, key.getApplicationKey() );
        builder.key( key );
        return builder.build();
    }

    private LayoutDescriptor parseLayoutDescriptor( final DescriptorKey key, final String resource )
    {
        return YmlLayoutDescriptorParser.parse( resource, key.getApplicationKey() ).key( key ).build();
    }

    private ContentType parseContentTypeDescriptor( final ContentTypeName name, final String resource )
    {
        try
        {
            final ContentType.Builder builder = YmlContentTypeParser.parse( resource, name.getApplicationKey() );
            builder.name( name );
            return builder.build();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( String.format( "Could not parse dynamic content type [%s]", name ), e );
        }
    }

    private FormFragmentDescriptor parseFormFragmentDescriptor( final FormFragmentName name, final String resource )
    {
        return YmlFormFragmentParser.parse( resource, name.getApplicationKey() ).name( name ).build();
    }

    private XData parseXDataDescriptor( final XDataName name, final String resource )
    {
        return YmlXDataParser.parse( resource, name.getApplicationKey() ).name( name ).build();
    }

    private CmsDescriptor parseCmsDescriptor( final ApplicationKey applicationKey, final String resource )
    {
        return YmlCmsDescriptorParser.parse( resource, applicationKey ).build();
    }

    private StyleDescriptor parseStylesDescriptor( final ApplicationKey applicationKey, final String resource )
    {
        return YmlStyleDescriptorParser.parse( resource, applicationKey ).build();
    }
}
