package com.enonic.xp.core.impl.content.index.processor;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SiteConfigProcessorTest
{
    private CmsService cmsService;

    private ApplicationKey applicationKey1;

    private ApplicationKey applicationKey2;

    @BeforeEach
    void setUp()
    {
        this.cmsService = Mockito.mock( CmsService.class );
        this.applicationKey1 = ApplicationKey.from( "applicationKey1" );
        this.applicationKey2 = ApplicationKey.from( "applicationKey2" );
    }

    @Test
    void test_empty_site_config()
    {
        final PatternIndexConfigDocument result = processConfigs( SiteConfigs.create().build(), Form.empty(), null );
        assertEquals( 0, result.getPathIndexConfigs().size() );
    }

    @Test
    void test_site_config_with_html_area()
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() ).build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form );
        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper", result.getConfigForPath(
                IndexPath.from( String.join( ".", ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );
    }

    @Test
    void test_multiple_site_configs_with_html_areas()
    {
        final Form form1 = Form.create()
            .addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();
        final Form form2 = Form.create()
            .addFormItem( Input.create().name( "text2" ).label( "text2" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() )
            .add( SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build() )
            .build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form1, form2 );
        assertEquals( 2, result.getPathIndexConfigs().size() );

        assertEquals( "htmlStripper", result.getConfigForPath(
                IndexPath.from( String.join( ".", ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );

        assertEquals( "htmlStripper", result.getConfigForPath(
                IndexPath.from( String.join( ".", ContentPropertyNames.DATA, SITECONFIG, "config", "text2" ) ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );
    }

    @Test
    void test_multiple_site_configs_with_same_path()
    {
        final Form form1 = Form.create()
            .addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();
        final Form form2 = Form.create()
            .addFormItem( Input.create().name( "text1" ).label( "text1" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() )
            .add( SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build() )
            .build();

        final PatternIndexConfigDocument result = processConfigs( siteConfigs, form1, form2 );
        assertEquals( 1, result.getPathIndexConfigs().size() );

        assertEquals( "htmlStripper", result.getConfigForPath(
                IndexPath.from( String.join( ".", ContentPropertyNames.DATA, SITECONFIG, "config", "text1" ) ) )
            .getIndexValueProcessors()
            .get( 0 )
            .getName() );

    }

    private Collection<Form> getConfigForms( final CmsService cmsService, final SiteConfigs siteConfigs )
    {
        return siteConfigs.stream()
            .map( siteConfig -> cmsService.getDescriptor( siteConfig.getApplicationKey() ).getForm() )
            .collect( Collectors.toList() );
    }

    private PatternIndexConfigDocument processConfigs( final SiteConfigs siteConfigs, final Form... forms )
    {
        for ( int i = 0; i < siteConfigs.getSize(); i++ )
        {
            final SiteConfig siteConfig = siteConfigs.get( i );
            final CmsDescriptor descriptor = CmsDescriptor.create().applicationKey( siteConfig.getApplicationKey() ).form( forms[i] ).build();
            Mockito.when( cmsService.getDescriptor( siteConfig.getApplicationKey() ) ).thenReturn( descriptor );
        }

        final CmsConfigProcessor configProcessor = new CmsConfigProcessor( getConfigForms( cmsService, siteConfigs ) );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
