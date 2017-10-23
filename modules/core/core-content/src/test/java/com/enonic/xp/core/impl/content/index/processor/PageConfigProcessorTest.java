package com.enonic.xp.core.impl.content.index.processor;

import java.util.Set;

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

    private PageConfigProcessor configProcessor;

    private PatternIndexConfigDocument.Builder builder;

    @Before
    public void setUp()
        throws Exception
    {
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.descriptorKey = DescriptorKey.from( "descriptorKey" );
        this.configProcessor =
            PageConfigProcessor.create().pageDescriptorService( pageDescriptorService ).descriptorKey( descriptorKey ).build();
        this.builder = PatternIndexConfigDocument.create();
    }

    @Test
    public void test_empty_page_config_form()
        throws Exception
    {
        final Form form = Form.create().build();

        final PageDescriptor descriptor =
            PageDescriptor.create().key( descriptorKey ).config( form ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( descriptorKey ) ).thenReturn( descriptor );

        configProcessor.processDocument( builder );

        Set<PathIndexConfig> indexConfigs = builder.build().getPathIndexConfigs();

        assertEquals( 5, builder.build().getPathIndexConfigs().size() );

        assertTrue(
            indexConfigs.contains( PathIndexConfig.create().path( PropertyPath.from( PAGE ) ).indexConfig( IndexConfig.NONE ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE, "controller" ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE_CONFIG, "*" ) ).indexConfig( IndexConfig.BY_TYPE ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN ) ).indexConfig(
                PageConfigProcessor.TEXT_COMPONENT_INDEX_CONFIG ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( PAGE, "regions" ) ).indexConfig( IndexConfig.NONE ).build() ) );

    }

    @Test
    public void test_page_config_form_with_html_area()
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

        final PageDescriptor descriptor =
            PageDescriptor.create().key( descriptorKey ).config( form ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( descriptorKey ) ).thenReturn( descriptor );

        configProcessor.processDocument( builder );

        assertEquals( 6, builder.build().getPathIndexConfigs().size() );

        assertEquals( "htmlStripper",
                      builder.build().getConfigForPath( PropertyPath.from( PAGE + ".config.htmlArea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );
    }

  /*  @Test
    public void test_data_form_with_html_area()
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

        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "typeName" ).form( form ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        configProcessor.processDocument( builder );

        assertEquals( 2, builder.build().getPathIndexConfigs().size() );
        assertEquals( "htmlStripper",
                      builder.build().getConfigForPath( PropertyPath.from( DATA + ".htmlArea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }*/
}
