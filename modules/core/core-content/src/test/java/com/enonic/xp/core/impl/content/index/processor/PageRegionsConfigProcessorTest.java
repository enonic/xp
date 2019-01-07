package com.enonic.xp.core.impl.content.index.processor;

import java.util.Arrays;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponentType;

import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.ID;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.TEXT_COMPONENT_INDEX_CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.VALUE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class PageRegionsConfigProcessorTest
{
    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private Form configFormWithHtmlArea;

    @Before
    public void setUp()
        throws Exception
    {
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        Input myTextLine = Input.create().
            name( "htmlArea" ).
            inputType( InputTypeName.HTML_AREA ).
            label( "htmlArea" ).
            required( true ).
            build();
        this.configFormWithHtmlArea = Form.create().
            addFormItem( myTextLine ).
            build();
    }

    @Test
    public void test_size()
        throws Exception
    {
        final PatternIndexConfigDocument result = processPage( Page.create().regions( PageRegions.create().build() ).build(), null, null );
        assertEquals( 5, result.getPathIndexConfigs().size() );
    }

    @Test
    public void test_text_component()
        throws Exception
    {
        final PatternIndexConfigDocument result = processPage( Page.create().regions( PageRegions.create().build() ).build(), null, null );

        assertEquals( TEXT_COMPONENT_INDEX_CONFIG,
                      result.getConfigForPath( PropertyPath.from( COMPONENTS, TextComponentType.INSTANCE.toString(), VALUE ) ) );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, TextComponentType.INSTANCE.toString(), VALUE ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void test_part_component()
        throws Exception
    {
        final DescriptorKey partDescriptorKey = DescriptorKey.from( "part1AppKey:name" );
        final String htmlarea = "htmlarea";

        final Page page = Page.
            create().
            regions( PageRegions.create().
                add( Region.create().
                    name( "region1" ).
                    add( PartComponent.create().
                        name( "part1" ).
                        descriptor( partDescriptorKey ).
                        build() ).
                    build() ).
                build() ).
            build();

        final PatternIndexConfigDocument result = processPage( page, Arrays.asList( configFormWithHtmlArea ).listIterator(), null );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, PartComponentType.INSTANCE.toString(), CONFIG, partDescriptorKey.getApplicationKey().toString(),
                               htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void test_layout_component()
        throws Exception
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutAppKey:name" );
        final String htmlarea = "htmlarea";

        final Page page = Page.
            create().
            regions( PageRegions.create().
                add( Region.create().
                    name( "region1" ).
                    add( LayoutComponent.create().
                        name( "layout1" ).
                        descriptor( layoutDescriptorKey ).
                        regions( LayoutRegions.create().build() ).
                        build() ).
                    build() ).
                build() ).
            build();

        final PatternIndexConfigDocument result = processPage( page, null, Arrays.asList( configFormWithHtmlArea ).listIterator() );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, LayoutComponentType.INSTANCE.toString(), CONFIG,
                               layoutDescriptorKey.getApplicationKey().toString(), htmlarea ) ).getIndexValueProcessors().get(
            0 ).getName() );
    }

    @Test
    public void test_part_inside_layout_component()
        throws Exception
    {
        final String appKeyLayout = "layoutAppKey";
        final String appKeyPart = "partAppKey";
        final String htmlarea = "htmlarea";

        final Page page = Page.
            create().
            regions( PageRegions.create().
                add( Region.create().
                    name( "region1" ).
                    add( LayoutComponent.create().
                        name( "layout1" ).
                        descriptor( DescriptorKey.from( appKeyLayout ) ).
                        regions( LayoutRegions.create().
                            add( Region.create().
                                name( "layoutRegion1" ).
                                add( PartComponent.create().
                                    name( "part1" ).
                                    descriptor( DescriptorKey.from( appKeyPart ) ).
                                    build() ).
                                build() ).
                            build() ).
                        build() ).
                    build() ).
                build() ).
            build();

        final PatternIndexConfigDocument result = processPage( page, Arrays.asList( configFormWithHtmlArea ).listIterator(),
                                                               Arrays.asList( configFormWithHtmlArea ).listIterator() );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, LayoutComponentType.INSTANCE.toString(), CONFIG, appKeyLayout,
                               htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, PartComponentType.INSTANCE.toString(), CONFIG, appKeyPart,
                               htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void test_fragment_component()
        throws Exception
    {
        final Page page = Page.
            create().
            regions( PageRegions.create().
                add( Region.create().
                    name( "region1" ).
                    add( FragmentComponent.create().
                        name( "part1" ).
                        fragment( ContentId.from( "content-id" ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        final PatternIndexConfigDocument result = processPage( page, singletonList( configFormWithHtmlArea ).listIterator(), null );

        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( COMPONENTS, FragmentComponentType.INSTANCE.toString(), ID ) ).indexConfig(
                IndexConfig.MINIMAL ).build() ) );

    }

    private PatternIndexConfigDocument processPage( final Page page, final ListIterator<Form> partForms,
                                                    final ListIterator<Form> layoutForms )
    {
        final RegionDescriptors.Builder regionDescriptorsBuilder = RegionDescriptors.create();
        page.getRegions().forEach( region -> {

            region.getComponents().forEach( component -> {
                if ( PartComponentType.INSTANCE == component.getType() )
                {
                    if ( partForms != null && partForms.hasNext() )
                    {
                        final PartComponent partComponent = (PartComponent) component;

                        Mockito.when( partDescriptorService.getByKey( ( partComponent.getDescriptor() ) ) ).thenReturn(
                            PartDescriptor.create().key( partComponent.getDescriptor() ).config( partForms.next() ).build() );
                    }
                }
                else if ( LayoutComponentType.INSTANCE == component.getType() )
                {
                    if ( layoutForms != null && layoutForms.hasNext() )
                    {
                        final LayoutComponent layoutComponent = (LayoutComponent) component;

                        Mockito.when( layoutDescriptorService.getByKey( ( layoutComponent.getDescriptor() ) ) ).thenReturn(
                            LayoutDescriptor.create().key( layoutComponent.getDescriptor() ).config( layoutForms.next() ).regions(
                                RegionDescriptors.create().build() ).build() );

                        if ( layoutComponent.hasRegions() )
                        {
                            processLayoutRegions( layoutComponent.getRegions(), partForms );
                        }
                    }
                }

            } );

            regionDescriptorsBuilder.add( RegionDescriptor.create().name( region.getName() ).build() );
        } );

        final PageRegionsConfigProcessor configProcessor =
            new PageRegionsConfigProcessor( page, partDescriptorService, layoutDescriptorService );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }

    private void processLayoutRegions( final LayoutRegions layoutRegions, ListIterator<Form> partForms )
    {
        layoutRegions.forEach( layoutRegion -> {
            layoutRegion.getComponents().forEach( component -> {
                if ( PartComponentType.INSTANCE == component.getType() )
                {
                    if ( partForms != null && partForms.hasNext() )
                    {
                        final PartComponent partComponent = (PartComponent) component;

                        Mockito.when( partDescriptorService.getByKey( ( partComponent.getDescriptor() ) ) ).thenReturn(
                            PartDescriptor.create().key( partComponent.getDescriptor() ).config( partForms.next() ).build() );
                    }
                }
            } );
        } );
    }
}

