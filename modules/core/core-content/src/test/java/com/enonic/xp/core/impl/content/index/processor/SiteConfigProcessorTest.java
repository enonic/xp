package com.enonic.xp.core.impl.content.index.processor;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
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
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_CONFIG;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static org.junit.Assert.*;

public class SiteConfigProcessorTest
{
    private SiteService siteService;

    private ApplicationKey applicationKey1;

    private ApplicationKey applicationKey2;

    @Before
    public void setUp()
        throws Exception
    {
        this.siteService = Mockito.mock( SiteService.class );
        this.applicationKey1 = ApplicationKey.from( "applicationKey1" );
        this.applicationKey2 = ApplicationKey.from( "applicationKey2" );
    }

    @Test
    public void test_empty_site_config()
        throws Exception
    {
        final PatternIndexConfigDocument result = processConfigs( SiteConfigs.create().build(), Form.create().build(), null );
        assertEquals( 0, result.getPathIndexConfigs().size() );
    }

    @Test
    public void test_site_config_with_html_area()
        throws Exception
    {
        final Form form = Form.create().
            addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() ).
            build();

        final SiteConfigs siteConfigs = SiteConfigs.create().
            add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() ).
            build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form );
        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ).getIndexValueProcessors().get(
            0 ).getName() );
    }

    @Test
    public void test_multiple_site_configs_with_html_areas()
        throws Exception
    {
        final Form form1 = Form.create().
            addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() ).
            build();
        final Form form2 = Form.create().
            addFormItem( Input.create().name( "text2" ).label( "text2" ).inputType( InputTypeName.HTML_AREA ).build() ).
            build();

        final SiteConfigs siteConfigs = SiteConfigs.create().
            add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() ).
            add( SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build() ).
            build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form1, form2 );
        assertEquals( 2, result.getPathIndexConfigs().size() );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ).getIndexValueProcessors().get(
            0 ).getName() );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, SITECONFIG, "config", "text2" ) ).getIndexValueProcessors().get(
            0 ).getName() );
    }

    @Test
    public void test_multiple_site_configs_with_same_path()
        throws Exception
    {
        final Form form1 = Form.create().
            addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() ).
            build();
        final Form form2 = Form.create().
            addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() ).
            build();

        final SiteConfigs siteConfigs = SiteConfigs.create().
            add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() ).
            add( SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build() ).
            build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form1, form2 );
        assertEquals( 1, result.getPathIndexConfigs().size() );

        assertEquals( "htmlStripper", result.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ).getIndexValueProcessors().get( 0 ).getName() );

    }

    private Collection<Form> getConfigForms( final SiteService siteService, final SiteConfigs siteConfigs )
    {
        return siteConfigs.stream().map( siteConfig -> siteService.getDescriptor( siteConfig.getApplicationKey() ).getForm() ).collect(
            Collectors.toList() );
    }

    private PatternIndexConfigDocument processConfigs( final SiteConfigs siteConfigs, final Form... forms )
    {
        for(int i = 0; i < siteConfigs.getSize(); i++) {
            final SiteConfig siteConfig = siteConfigs.get( i );
            final SiteDescriptor descriptor = SiteDescriptor.create().form( forms[i] ).build();
            Mockito.when( siteService.getDescriptor( siteConfig.getApplicationKey() ) ).thenReturn( descriptor );
        }

        final SiteConfigProcessor configProcessor = new SiteConfigProcessor( getConfigForms( siteService, siteConfigs ) );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
