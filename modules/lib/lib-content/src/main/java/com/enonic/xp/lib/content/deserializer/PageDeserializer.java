package com.enonic.xp.lib.content.deserializer;

import java.util.List;
import java.util.Map;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.region.TextComponent;

public final class PageDeserializer
{
    private final PropertyTreeTranslator translator;

    public PageDeserializer( final PropertyTreeTranslator translator )
    {
        this.translator = translator;
    }

    public Page deserialize( final Map<String, Object> map )
    {
        if ( map == null || map.isEmpty() )
        {
            return null;
        }

        final Page.Builder builder = Page.create();

        // descriptor
        final Object descriptor = map.get( "descriptor" );
        if ( descriptor instanceof String )
        {
            builder.descriptor( DescriptorKey.from( (String) descriptor ) );
        }

        // template
        final Object template = map.get( "template" );
        if ( template instanceof String )
        {
            builder.template( PageTemplateKey.from( (String) template ) );
        }

        // config
        final Object config = map.get( "config" );
        if ( config instanceof Map )
        {
            builder.config( translator.translate( (Map<String, Object>) config ) );
        }

        // regions
        final Object regions = map.get( "regions" );
        if ( regions instanceof Map )
        {
            builder.regions( deserializeRegions( (Map<String, Object>) regions ) );
        }

        // fragment
        final Object fragment = map.get( "fragment" );
        if ( fragment instanceof Map )
        {
            builder.fragment( deserializeComponent( (Map<String, Object>) fragment ) );
        }

        return builder.build();
    }

    public Regions deserializeRegions( final Map<String, Object> regions )
    {
        final Regions.Builder builder = Regions.create();
        regions.forEach( ( regionName, regionObj ) -> {
            if ( !( regionObj instanceof Map ) )
            {
                return;
            }
            final Region region = createRegion( regionName, (Map<String, Object>) regionObj );
            builder.add( region );
        } );
        return builder.build();
    }

    private Region createRegion( final String name, final Map<String, Object> regionMap )
    {
        final Region.Builder builder = Region.create().name( name );
        final Object components = regionMap.get( "components" );
        if ( components instanceof List )
        {
            for ( Object comp : (List<?>) components )
            {
                if ( comp instanceof Map )
                {
                    builder.add( deserializeComponent( (Map<String, Object>) comp ) );
                }
            }
        }
        return builder.build();
    }

    public Component deserializeComponent( final Map<String, Object> map )
    {
        final String type = (String) map.get( "type" );
        if ( type == null )
        {
            throw new IllegalArgumentException( "Missing 'type' in component" );
        }

        switch ( type )
        {
            case "layout":
                return createLayoutComponent( map );
            case "part":
                return createPartComponent( map );
            case "text":
                return createTextComponent( map );
            case "image":
                return createImageComponent( map );
            case "fragment":
                return createFragmentComponent( map );
            default:
                throw new UnsupportedOperationException( "Unsupported component type: " + type );
        }
    }

    private LayoutComponent createLayoutComponent( final Map<String, Object> map )
    {
        final LayoutComponent.Builder builder = LayoutComponent.create();

        final Object descriptor = map.get( "descriptor" );
        if ( descriptor instanceof String )
        {
            builder.descriptor( DescriptorKey.from( (String) descriptor ) );
        }

        final Object config = map.get( "config" );
        if ( config instanceof Map )
        {
            builder.config( translator.translate( (Map<String, Object>) config ) );
        }

        final Object regions = map.get( "regions" );
        if ( regions instanceof Map )
        {
            builder.regions( deserializeRegions( (Map<String, Object>) regions ) );
        }

        return builder.build();
    }

    private PartComponent createPartComponent( final Map<String, Object> map )
    {
        final PartComponent.Builder builder = PartComponent.create();

        final Object descriptor = map.get( "descriptor" );
        if ( descriptor instanceof String )
        {
            builder.descriptor( (String) descriptor );
        }

        final Object config = map.get( "config" );
        if ( config instanceof Map )
        {
            builder.config( translator.translate( (Map<String, Object>) config ) );
        }

        return builder.build();
    }

    private TextComponent createTextComponent( final Map<String, Object> map )
    {
        final TextComponent.Builder builder = TextComponent.create();

        final Object text = map.get( "text" );
        if ( text != null )
        {
            builder.text( text.toString() );
        }

        return builder.build();
    }

    private ImageComponent createImageComponent( final Map<String, Object> map )
    {
        final ImageComponent.Builder builder = ImageComponent.create();

        final Object image = map.get( "image" );
        if ( image instanceof String )
        {
            builder.image( ContentId.from( (String) image ) );
        }

        final Object config = map.get( "config" );
        if ( config instanceof Map )
        {
            builder.config( translator.translate( (Map<String, Object>) config ) );
        }

        return builder.build();
    }

    private FragmentComponent createFragmentComponent( final Map<String, Object> map )
    {
        final FragmentComponent.Builder builder = FragmentComponent.create();

        final Object fragment = map.get( "fragment" );
        if ( fragment instanceof String )
        {
            builder.fragment( ContentId.from( (String) fragment ) );
        }

        return builder.build();
    }
}
