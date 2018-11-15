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
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.appNameToConfigPropertyName;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageRegionsConfigProcessor
    implements ContentIndexConfigProcessor
{
    public static final String COMPONENTS = "components";

    public static final String CONFIG = "config";

    public static final String LAYOUT_COMPONENT = "layout";

    public static final String PART_COMPONENT = "part";

    public static final String TEXT_COMPONENT = "text";

    public static final String FRAGMENT_COMPONENT = "fragment";

    public static final String ANY_PATH_PATTERN = "**";

    public static final String LAYOUT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, COMPONENTS, ANY_PATH_PATTERN, LAYOUT_COMPONENT );

    public static final String PART_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, COMPONENTS, ANY_PATH_PATTERN, PART_COMPONENT );

    public static final String TEXT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, COMPONENTS, ANY_PATH_PATTERN, TEXT_COMPONENT );

    public static final String FRAGMENT_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, COMPONENTS, ANY_PATH_PATTERN, FRAGMENT_COMPONENT );

    public static final String PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN = String.join( ELEMENT_DIVIDER, TEXT_COMPONENT_PATH, "value" );

    public final static IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
        addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
        build();

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final Page page;

    public static void main( String[] args )
    {
        System.out.println();
    }

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

        pageRegions.forEach( pageRegion -> processComponents( pageRegion.getComponents(), builder ) );
    }

    private void parseLayoutRegions( final LayoutRegions layoutRegions, final PatternIndexConfigDocument.Builder builder )
    {
        if ( layoutRegions == null )
        {
            return;
        }

        layoutRegions.forEach( pageRegion -> processComponents( pageRegion.getComponents(), builder ) );
    }

    private void processComponents( final List<Component> components, final PatternIndexConfigDocument.Builder builder )
    {
        if ( components == null )
        {
            return;
        }

        components.forEach( component -> {
            if ( PartComponentType.INSTANCE == component.getType() )
            {
                final DescriptorKey descriptorKey = ( (PartComponent) component ).getDescriptor();

                if ( descriptorKey != null )
                {
                    final PartDescriptor partDescriptor = partDescriptorService.getByKey( ( (PartComponent) component ).getDescriptor() );
                    final String appKeyAsString = appNameToConfigPropertyName( descriptorKey );

                    final IndexConfigVisitor indexConfigVisitor =
                        new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, PART_COMPONENT_PATH, CONFIG, appKeyAsString ), builder );
                    indexConfigVisitor.traverse( partDescriptor.getConfig() );

                    builder.add( String.join( ELEMENT_DIVIDER, PART_COMPONENT_PATH, CONFIG, appKeyAsString, ALL_PATTERN ),
                                 IndexConfig.BY_TYPE );
                }
            }
            if ( LayoutComponentType.INSTANCE == component.getType() )
            {
                final DescriptorKey descriptorKey = ( (LayoutComponent) component ).getDescriptor();

                if ( descriptorKey != null )
                {
                    final LayoutDescriptor layoutDescriptor =
                        layoutDescriptorService.getByKey( ( (LayoutComponent) component ).getDescriptor() );
                    final String appKeyAsString = appNameToConfigPropertyName( descriptorKey );

                    final IndexConfigVisitor indexConfigVisitor =
                        new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT_PATH, CONFIG, appKeyAsString ), builder );
                    indexConfigVisitor.traverse( layoutDescriptor.getConfig() );

                    builder.add( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT_PATH, CONFIG, appKeyAsString, ALL_PATTERN ),
                                 IndexConfig.BY_TYPE );

                    parseLayoutRegions( ( (LayoutComponent) component ).getRegions(), builder );
                }
            }
        } );
    }
}
