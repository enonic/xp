package com.enonic.xp.core.impl.content.index.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.RegionDescriptors;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.ALL_PATTERN;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.CONFIG;
import static com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor.DESCRIPTOR;
import static org.junit.Assert.*;

public class PageConfigProcessorTest
{
    private PageDescriptorService pageDescriptorService;

    private DescriptorKey descriptorKey;

    @Before
    public void setUp()
        throws Exception
    {
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.descriptorKey = DescriptorKey.from( "descriptorKey" );
    }

    @Test
    public void test_size()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertEquals( 4, result.getPathIndexConfigs().size() );
    }

    @Test
    public void test_page()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( COMPONENTS ) ).indexConfig( IndexConfig.NONE ).build() ) );
    }

    @Test
    public void test_page_descriptor()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( COMPONENTS, PAGE, DESCRIPTOR ) ).indexConfig(
                IndexConfig.MINIMAL ).build() ) );
    }

    @Test
    public void test_page_config_form_with_html_area_simple()
        throws Exception
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
        assertTrue( result.getPathIndexConfigs().contains( PathIndexConfig.create().path(
            PropertyPath.from( COMPONENTS, PAGE, CONFIG, descriptorKey.getApplicationKey().toString(), ALL_PATTERN ) ).indexConfig(
            IndexConfig.BY_TYPE ).build() ) );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( COMPONENTS, PAGE, CONFIG, descriptorKey.getApplicationKey().toString(),
                               "htmlarea" ) ).getIndexValueProcessors().get( 0 ).getName() );
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
    }

    private PatternIndexConfigDocument processForm( final Form form )
    {
        final PageDescriptor descriptor =
            PageDescriptor.create().key( descriptorKey ).config( form ).regions( RegionDescriptors.create().build() ).build();

        final Page page = Page.create().descriptor( descriptorKey ).build();

        Mockito.when( pageDescriptorService.getByKey( descriptorKey ) ).thenReturn( descriptor );

        final PageConfigProcessor configProcessor =
            new PageConfigProcessor( page, getPageConfigForm( pageDescriptorService, descriptorKey ) );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
