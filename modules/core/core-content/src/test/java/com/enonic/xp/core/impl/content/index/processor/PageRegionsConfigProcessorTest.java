package com.enonic.xp.core.impl.content.index.processor;

import java.util.Arrays;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
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
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponentType;

import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.ID;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.TEXT_COMPONENT_INDEX_CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.VALUE;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageRegionsConfigProcessorTest
{
    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private Form configFormWithHtmlArea;

    private Form configFormWithTextInput;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        this.configFormWithHtmlArea = Form.create()
            .addFormItem(
                Input.create().name( "htmlArea" ).inputType( InputTypeName.HTML_AREA ).label( "htmlArea" ).required( true ).build() )
            .build();
        this.configFormWithTextInput = Form.create()
            .addFormItem(
                Input.create().name( "textArea" ).inputType( InputTypeName.TEXT_AREA ).label( "textArea" ).required( true ).build() )
            .build();
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

        assertEquals( "htmlStripper",
                      result.getConfigForPath( PropertyPath.from( COMPONENTS, TextComponentType.INSTANCE.toString(), VALUE ) )
                          .getIndexValueProcessors()
                          .get( 0 )
                          .getName() );
    }

    @Test
    public void test_part_component()
        throws Exception
    {
        final DescriptorKey partDescriptorKey = DescriptorKey.from( "part1AppKey:name" );
        final String htmlarea = "htmlarea";

        final Page page = Page.create()
            .regions( PageRegions.create()
                          .add( Region.create()
                                    .name( "region1" )
                                    .add( PartComponent.create().descriptor( partDescriptorKey ).build() )
                                    .build() )
                          .build() )
            .build();

        final PatternIndexConfigDocument result = processPage( page, Arrays.asList( configFormWithHtmlArea ).listIterator(), null );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, PartComponentType.INSTANCE.toString(), CONFIG, partDescriptorKey.getApplicationKey().toString(),
                               partDescriptorKey.getName(), htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void test_layout_component()
        throws Exception
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutAppKey:name" );
        final String htmlarea = "htmlarea";

        final Page page = Page.create()
            .regions( PageRegions.create()
                          .add( Region.create()
                                    .name( "region1" )
                                    .add( LayoutComponent.create()
                                              .descriptor( layoutDescriptorKey )
                                              .regions( LayoutRegions.create().build() )
                                              .build() )
                                    .build() )
                          .build() )
            .build();

        final PatternIndexConfigDocument result = processPage( page, null, Arrays.asList( configFormWithHtmlArea ).listIterator() );

        assertEquals( "htmlStripper", result.getConfigForPath(
                PropertyPath.from( COMPONENTS, LayoutComponentType.INSTANCE.toString(), CONFIG,
                                   layoutDescriptorKey.getApplicationKey().toString(), layoutDescriptorKey.getName(), htmlarea ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );
    }

    @Test
    public void test_part_inside_layout_component()
        throws Exception
    {
        final DescriptorKey layoutKey = DescriptorKey.from( "app:layoutAppKey" );
        final DescriptorKey partKey = DescriptorKey.from( "app:partAppKey" );
        final String htmlarea = "htmlarea";

        final Page page = Page.create()
            .regions( PageRegions.create()
                          .add( Region.create()
                                    .name( "region1" )
                                    .add( LayoutComponent.create()
                                              .descriptor( layoutKey )
                                              .regions( LayoutRegions.create()
                                                            .add( Region.create()
                                                                      .name( "layoutRegion1" )
                                                                      .add( PartComponent.create().descriptor( partKey ).build() )
                                                                      .build() )
                                                            .build() )
                                              .build() )
                                    .build() )
                          .build() )
            .build();

        final PatternIndexConfigDocument result = processPage( page, Arrays.asList( configFormWithHtmlArea ).listIterator(),
                                                               Arrays.asList( configFormWithHtmlArea ).listIterator() );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, LayoutComponentType.INSTANCE.toString(), CONFIG, layoutKey.getApplicationKey().toString(),
                               layoutKey.getName(), htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, PartComponentType.INSTANCE.toString(), CONFIG, partKey.getApplicationKey().toString(),
                               partKey.getName(), htmlarea ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void test_fragment_component()
        throws Exception
    {
        final Page page = Page.create()
            .regions( PageRegions.create()
                          .add( Region.create()
                                    .name( "region1" )
                                    .add( FragmentComponent.create().fragment( ContentId.from( "content-id" ) ).build() )
                                    .build() )
                          .build() )
            .build();

        final PatternIndexConfigDocument result = processPage( page, singletonList( configFormWithHtmlArea ).listIterator(), null );

        assertTrue( result.getPathIndexConfigs()
                        .contains( PathIndexConfig.create()
                                       .path( PropertyPath.from( COMPONENTS, FragmentComponentType.INSTANCE.toString(), ID ) )
                                       .indexConfig( IndexConfig.MINIMAL )
                                       .build() ) );

    }

    @Test
    public void test_fragment_page()
        throws Exception
    {
        final DescriptorKey layoutKey = DescriptorKey.from( "app:layoutAppKey" );
        final DescriptorKey partKey = DescriptorKey.from( "app:partAppKey" );

        final Page page = Page.create()
            .regions( PageRegions.create().build() )
            .fragment( LayoutComponent.create()
                           .descriptor( layoutKey )
                           .regions( LayoutRegions.create()
                                         .add( Region.create()
                                                   .name( "layoutRegion1" )
                                                   .add( PartComponent.create().descriptor( partKey ).build() )
                                                   .build() )
                                         .build() )
                           .build() )
            .build();

        final PatternIndexConfigDocument result = processPage( page, Arrays.asList( configFormWithHtmlArea ).listIterator(),
                                                               Arrays.asList( configFormWithHtmlArea ).listIterator() );

        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( "components.part.config.app.partAppKey" ) ) );
        assertEquals( "htmlStripper", result.getConfigForPath( PropertyPath.from( "components.part.config.app.partappkey.htmlarea" ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );

        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( "components.layout.config.app.layoutAppKey" ) ) );
        assertEquals( "htmlStripper", result.getConfigForPath( PropertyPath.from( "components.layout.config.app.layoutAppKey.htmlarea" ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );
    }

    @Test
    public void test_complex_component_config()
        throws Exception
    {
        final PropertyTree config = new PropertyTree();
        final PropertySet items = config.addSet( "items" );
        items.addStrings( "input", "a", "b", "c" );

        final Page page = Page.create()
            .regions( PageRegions.create()
                          .add( Region.create()
                                    .name( "region1" )
                                    .add( PartComponent.create()
                                              .descriptor( DescriptorKey.from( "appKey1:partName1" ) )
                                              .config( new PropertyTree() )
                                              .build() )
                                    .add( LayoutComponent.create()
                                              .descriptor( DescriptorKey.from( "appKey2:layoutName" ) )
                                              .config( new PropertyTree() )
                                              .regions( LayoutRegions.create()
                                                            .add( Region.create()
                                                                      .name( "region" )
                                                                      .add( PartComponent.create()
                                                                                .descriptor( DescriptorKey.from( "appKey3:partName2" ) )
                                                                                .config( new PropertyTree() )
                                                                                .build() )
                                                                      .build() )
                                                            .build() )
                                              .build() )
                                    .build() )
                          .build() )
            .build();

        final Form form = Form.create()
            .addFormItem( FormItemSet.create()
                              .name( "items" )
                              .addFormItem( Input.create()
                                                .name( "input" )
                                                .label( "input" )
                                                .inputType( InputTypeName.TEXT_LINE )
                                                .occurrences( 0, 5 )
                                                .build() )
                              .build() )
            .build();

        final PatternIndexConfigDocument result =
            processPage( page, Arrays.asList( form, form ).listIterator(), singletonList( form ).listIterator() );
        assertEquals( 8, result.getPathIndexConfigs().size() );

        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( "components.part.config.appKey1.partName1" ) ) );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( "components.part.config.appKey2.layoutName" ) ) );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( "components.part.config.appkey3.partname2" ) ) );
    }

    private PatternIndexConfigDocument processPage( final Page page, final ListIterator<Form> partForms,
                                                    final ListIterator<Form> layoutForms )
    {
        page.getRegions()
            .forEach( region -> region.getComponents().forEach( component -> processComponent( partForms, layoutForms, component ) ) );

        if ( page.isFragment() )
        {
            processComponent( partForms, layoutForms, page.getFragment() );
        }

        final PageRegionsConfigProcessor configProcessor =
            new PageRegionsConfigProcessor( page, partDescriptorService, layoutDescriptorService );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }

    private void processComponent( final ListIterator<Form> partForms, final ListIterator<Form> layoutForms, final Component component )
    {
        if ( PartComponentType.INSTANCE == component.getType() )
        {
            if ( partForms != null && partForms.hasNext() )
            {
                final PartComponent partComponent = (PartComponent) component;

                Mockito.when( partDescriptorService.getByKey( partComponent.getDescriptor() ) )
                    .thenReturn( PartDescriptor.create().key( partComponent.getDescriptor() ).config( partForms.next() ).build() );
            }
        }
        else if ( LayoutComponentType.INSTANCE == component.getType() )
        {
            if ( layoutForms != null && layoutForms.hasNext() )
            {
                final LayoutComponent layoutComponent = (LayoutComponent) component;

                Mockito.when( layoutDescriptorService.getByKey( layoutComponent.getDescriptor() ) )
                    .thenReturn( LayoutDescriptor.create()
                                     .key( layoutComponent.getDescriptor() )
                                     .config( layoutForms.next() )
                                     .regions( RegionDescriptors.create().build() )
                                     .build() );

                if ( layoutComponent.hasRegions() )
                {
                    processLayoutRegions( layoutComponent.getRegions(), partForms );
                }
            }
        }
    }

    private void processLayoutRegions( final LayoutRegions layoutRegions, ListIterator<Form> partForms )
    {
        layoutRegions.forEach( layoutRegion -> layoutRegion.getComponents().forEach( component -> {
            if ( PartComponentType.INSTANCE == component.getType() )
            {
                if ( partForms != null && partForms.hasNext() )
                {
                    final PartComponent partComponent = (PartComponent) component;

                    Mockito.when( partDescriptorService.getByKey( partComponent.getDescriptor() ) )
                        .thenReturn( PartDescriptor.create().key( partComponent.getDescriptor() ).config( partForms.next() ).build() );
                }
            }
        } ) );
    }
}

