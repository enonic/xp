package com.enonic.xp.core.impl.content.index.processor;

import java.util.List;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.TextComponentType;

import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.ALL_PATTERN;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.DESCRIPTOR;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.appNameToConfigPropertyName;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageRegionsConfigProcessor
    implements ContentIndexConfigProcessor
{
    static final String ID = "id";

    static final String VALUE = "value";

    static final IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
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

        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, ImageComponentType.INSTANCE.toString(), ID ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, FragmentComponentType.INSTANCE.toString(), ID ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, TextComponentType.INSTANCE.toString(), VALUE ),
                     TEXT_COMPONENT_INDEX_CONFIG );

        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, PartComponentType.INSTANCE.toString(), DESCRIPTOR ), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, LayoutComponentType.INSTANCE.toString(), DESCRIPTOR ), IndexConfig.MINIMAL );

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

    private void processComponents( final List<Component> components, final PatternIndexConfigDocument.Builder builder )
    {
        if ( components == null )
        {
            return;
        }

        components.stream().
            filter( component -> component instanceof DescriptorBasedComponent ).
            map( component -> (DescriptorBasedComponent) component ).
            filter( component -> component.hasDescriptor() ).
            forEach( component -> processDescriptorBasedComponent( component, builder ) );
    }

    private void processDescriptorBasedComponent( final DescriptorBasedComponent component,
                                                  final PatternIndexConfigDocument.Builder builder )
    {
        final String appKeyAsString = appNameToConfigPropertyName( component.getDescriptor() );

        final IndexConfigVisitor indexConfigVisitor =
            new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, COMPONENTS, component.getType().toString(), CONFIG, appKeyAsString ),
                                    builder );
        indexConfigVisitor.traverse( getComponentConfig( component ) );

        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, component.getType().toString(), CONFIG, appKeyAsString, ALL_PATTERN ),
                     IndexConfig.BY_TYPE );

        if ( component instanceof LayoutComponent )
        {
            parseLayoutRegions( ( (LayoutComponent) component ).getRegions(), builder );
        }
    }

    private Form getComponentConfig( final DescriptorBasedComponent component )
    {
        if ( component instanceof PartComponent )
        {
            return partDescriptorService.getByKey( component.getDescriptor() ).getConfig();
        }

        return layoutDescriptorService.getByKey( component.getDescriptor() ).getConfig();
    }

    private void parseLayoutRegions( final LayoutRegions layoutRegions, final PatternIndexConfigDocument.Builder builder )
    {
        if ( layoutRegions == null )
        {
            return;
        }

        layoutRegions.forEach( pageRegion -> processComponents( pageRegion.getComponents(), builder ) );
    }
}
