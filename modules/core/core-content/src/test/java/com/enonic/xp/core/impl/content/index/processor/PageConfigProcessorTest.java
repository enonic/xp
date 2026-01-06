package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.RegionDescriptors;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CUSTOMIZED;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.DESCRIPTOR;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageConfigProcessorTest
{
    private PageDescriptorService pageDescriptorService;

    private DescriptorKey descriptorKey;

    @BeforeEach
    void setUp()
    {
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.descriptorKey = DescriptorKey.from( "appKey:descriptorName" );
    }

    @Test
    void test_size()
    {
        final PatternIndexConfigDocument result = processForm( Form.empty() );
        assertEquals( 4, result.getPathIndexConfigs().size() );
    }

    @Test
    void test_empty_config()
    {
        final PatternIndexConfigDocument result = processForm( Form.empty() );

        assertEquals( 4, result.getPathIndexConfigs().size() );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS ) ).
                indexConfig( IndexConfig.NONE ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, DESCRIPTOR ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, CUSTOMIZED ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, TEMPLATE ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

    }

    @Test
    void test_config_with_data()
    {
        final PatternIndexConfigDocument result = processForm( Form.create().
            addFormItem( FormItemSet.create().
                name( "items" ).
                addFormItem( Input.create().
                    name( "input" ).
                    label( "input" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    occurrences( 0, 5 ).
                    build() ).
                build() ).
            build() );

        assertEquals( 5, result.getPathIndexConfigs().size() );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS ) ).
                indexConfig( IndexConfig.NONE ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, DESCRIPTOR ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, CUSTOMIZED ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

        assertTrue( result.getPathIndexConfigs().
            contains( PathIndexConfig.create().path( IndexPath.from( COMPONENTS, PAGE, TEMPLATE ) ).
                indexConfig( IndexConfig.MINIMAL ).
                build() ) );

        assertEquals( IndexConfig.BY_TYPE,
                      result.getConfigForPath( IndexPath.from( String.join( ".", COMPONENTS, PAGE, CONFIG, "appKey", "descriptorName" ) ) ) );
    }

    @Test
    void test_page_config_form_with_html_area_simple()
    {
        Input myTextLine = Input.create().
            name( "htmlArea" ).
            inputType( InputTypeName.HTML_AREA ).
            label( "htmlArea" ).
            required( true ).
            build();
        Form form = Form.create().
            addFormItem( myTextLine ).
            build();

        final PatternIndexConfigDocument result = processForm( form );

        assertEquals( 6, result.getPathIndexConfigs().size() );
        assertTrue( result.getPathIndexConfigs()
                        .contains( PathIndexConfig.create()
                                       .path( IndexPath.from( COMPONENTS, PAGE, CONFIG, descriptorKey.getApplicationKey().toString(),
                                                                 descriptorKey.getName() ) )
                                       .indexConfig( IndexConfig.BY_TYPE )
                                       .build() ) );
        assertEquals( "htmlStripper", result.getConfigForPath(
            IndexPath.from( String.join( "." , COMPONENTS, PAGE, CONFIG, descriptorKey.getApplicationKey().toString(), descriptorKey.getName(),
                               "htmlarea" ) ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
    }

    private PatternIndexConfigDocument processForm( final Form form )
    {
        final PageDescriptor descriptor = PageDescriptor.create().key( descriptorKey ).
            config( form ).
            regions( RegionDescriptors.create().build() ).
            build();

        final Page page = Page.create().
            descriptor( descriptorKey ).
            build();

        Mockito.when( pageDescriptorService.getByKey( descriptorKey ) ).thenReturn( descriptor );

        final PageConfigProcessor configProcessor =
            new PageConfigProcessor( page, getPageConfigForm( pageDescriptorService, descriptorKey ) );

        return configProcessor.processDocument( PatternIndexConfigDocument.empty() );
    }
}
