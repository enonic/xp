package com.enonic.xp.core.impl.content.index.processor;

import java.util.List;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;

import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.ALL_PATTERN;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageRegionsConfigProcessor
    implements ContentIndexConfigProcessor
{
    public static final String REGION = "region";

    public static final String COMPONENT = "component";

    public static final String CONFIG = "config";

    public static final String LAYOUT_COMPONENT = "LayoutComponent";

    public static final String PART_COMPONENT = "PartComponent";

    public static final String TEXT_COMPONENT = "TextComponent";

    public static final String FRAGMENT_COMPONENT = "FragmentComponent";

    public static final String ANY_PATH_PATTERN = "**";

    public static final String PAGE_REGION = String.join( ELEMENT_DIVIDER, "page", REGION );

    public static final String LAYOUT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, PAGE_REGION, ANY_PATH_PATTERN, LAYOUT_COMPONENT );

    public static final String PART_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, PAGE_REGION, ANY_PATH_PATTERN, PART_COMPONENT );

    public static final String TEXT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, PAGE_REGION, ANY_PATH_PATTERN, TEXT_COMPONENT );

    public static final String FRAGMENT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, PAGE_REGION, ANY_PATH_PATTERN, FRAGMENT_COMPONENT );

    public static final String PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN = String.join( ELEMENT_DIVIDER, TEXT_COMPONENT_PATH, "text" );

    public final static IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
        addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
        build();

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final Page page;

    public PageRegionsConfigProcessor( final Page page, final PartDescriptorService partDescriptorService,
                                       final LayoutDescriptorService layoutDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.page = page;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        if ( page == null )
        {
            return builder;
        }

        builder.add( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT_PATH, CONFIG, ALL_PATTERN ), IndexConfig.BY_TYPE );
        builder.add( String.join( ELEMENT_DIVIDER, PART_COMPONENT_PATH, CONFIG, ALL_PATTERN ), IndexConfig.BY_TYPE );

        builder.add( String.join( ELEMENT_DIVIDER, TEXT_COMPONENT_PATH, ALL_PATTERN ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, PART_COMPONENT_PATH, ALL_PATTERN ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT_PATH, ALL_PATTERN ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, FRAGMENT_COMPONENT_PATH, ALL_PATTERN ), IndexConfig.MINIMAL );

        builder.add( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN, TEXT_COMPONENT_INDEX_CONFIG );

        processPageRegions( page.getRegions(), builder );

        return builder;
    }

    private void processPageRegions( final PageRegions pageRegions, final PatternIndexConfigDocument.Builder builder )
    {
        if ( pageRegions == null )
        {
            return;
        }

        pageRegions.forEach( pageRegion -> processComponents( pageRegion.getComponents(), builder, PAGE_REGION ) );
    }

    private void parseLayoutRegions( final LayoutRegions layoutRegions, final PatternIndexConfigDocument.Builder builder, String path )
    {
        if ( layoutRegions == null )
        {
            return;
        }

        layoutRegions.forEach( pageRegion -> {
            final String layoutRegionPath = String.join( ELEMENT_DIVIDER, path, REGION );
            processComponents( pageRegion.getComponents(), builder, layoutRegionPath );
        } );
    }

    private void processComponents( final List<Component> components, final PatternIndexConfigDocument.Builder builder, String path )
    {
        if ( components == null )
        {
            return;
        }

        components.forEach( component -> {
            if ( PartComponentType.INSTANCE == component.getType() )
            {
                final String partComponentPath =
                    String.join( ELEMENT_DIVIDER, path, String.join( ELEMENT_DIVIDER, COMPONENT, PART_COMPONENT ) );

                final DescriptorKey descriptorKey = ( (PartComponent) component ).getDescriptor();

                if ( descriptorKey != null )
                {
                    final PartDescriptor partDescriptor = partDescriptorService.getByKey( ( (PartComponent) component ).getDescriptor() );

                    final IndexConfigVisitor indexConfigVisitor =
                        new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, partComponentPath, CONFIG ), builder );
                    indexConfigVisitor.traverse( partDescriptor.getConfig() );
                }
            }
            if ( LayoutComponentType.INSTANCE == component.getType() )
            {
                final String layoutComponentPath =
                    String.join( ELEMENT_DIVIDER, path, String.join( ELEMENT_DIVIDER, COMPONENT, LAYOUT_COMPONENT ) );

                final DescriptorKey descriptorKey = ( (LayoutComponent) component ).getDescriptor();

                if ( descriptorKey != null )
                {
                    final LayoutDescriptor layoutDescriptor =
                        layoutDescriptorService.getByKey( ( (LayoutComponent) component ).getDescriptor() );

                    final IndexConfigVisitor indexConfigVisitor =
                        new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, layoutComponentPath, CONFIG ), builder );
                    indexConfigVisitor.traverse( layoutDescriptor.getConfig() );

                    parseLayoutRegions( ( (LayoutComponent) component ).getRegions(), builder, layoutComponentPath );
                }
            }
        } );
    }
}
