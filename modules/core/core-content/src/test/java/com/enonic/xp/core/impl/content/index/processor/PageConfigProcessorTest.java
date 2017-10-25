package com.enonic.xp.core.impl.content.index.processor;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_CONFIG;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN;
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
        assertEquals( 5, result.getPathIndexConfigs().size() );
    }

    @Test
    public void test_page()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE ) ).indexConfig( IndexConfig.NONE ).build() ) );
    }

    @Test
    public void test_page_controller()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE, "controller" ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
    }

    @Test
    public void test_page_config()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE_CONFIG, "*" ) ).indexConfig( IndexConfig.BY_TYPE ).build() ) );
    }

    @Test
    public void test_text_component()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN ) ).indexConfig(
                PageConfigProcessor.TEXT_COMPONENT_INDEX_CONFIG ).build() ) );
    }

    @Test
    public void test_page_regions()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm( Form.create().build() );
        assertTrue( result.getPathIndexConfigs().contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE, "regions" ) ).indexConfig( IndexConfig.NONE ).build() ) );
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

        assertEquals( "htmlStripper",
                      result.getConfigForPath( PropertyPath.from( PAGE + ".config.htmlArea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );
    }

    @Test
    public void test_page_config_form_with_html_area_nested()
        throws Exception
    {
        final Form form = Form.create().
            addFormItem( FormItemSet.create().
                name( "region" ).
                addFormItem( FormItemSet.create().name( "textcomponent" ).
                    addFormItem( Input.create().name( "text" ).label( "text" ).inputType( InputTypeName.HTML_AREA ).build() ).
                    build() ).
                build() ).
            build();

        final PatternIndexConfigDocument result = processForm( form );

        assertEquals( IndexConfig.NONE, result.getConfigForPath( PropertyPath.from( ContentPropertyNames.PAGE ) ) );
        assertEquals( IndexConfig.NONE, result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "PartComponent", "template" ) ) );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "TextComponent", "text" ) ).getIndexValueProcessors().get(
            0 ).getName() );
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
    }

    private PatternIndexConfigDocument processForm( final Form form )
    {

        final PageDescriptor descriptor =
            PageDescriptor.create().key( descriptorKey ).config( form ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( descriptorKey ) ).thenReturn( descriptor );

        final PageConfigProcessor configProcessor = new PageConfigProcessor( getPageConfigForm( pageDescriptorService, descriptorKey ) );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
