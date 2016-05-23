package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorScriptFactory;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MacroInstructionTest
{
    private MacroDescriptorService macroDescriptorService;

    private MacroProcessorScriptFactory macroProcessorScriptFactory;

    private MacroInstruction macroInstruction;

    private PortalRequest portalRequest;

    private PortalUrlService portalUrlService;

    @Before
    public void setUp()
    {
        macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        macroProcessorScriptFactory = Mockito.mock( MacroProcessorScriptFactory.class );
        portalUrlService = Mockito.mock( PortalUrlService.class );

        Mockito.when( portalUrlService.assetUrl( Mockito.any() ) ).thenReturn( null );

        macroInstruction = new MacroInstruction();
        macroInstruction.setMacroDescriptorService( macroDescriptorService );
        macroInstruction.setMacroScriptFactory( macroProcessorScriptFactory );
        macroInstruction.setPortalUrlService( portalUrlService );

        portalRequest = new PortalRequest();
        final Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );
        portalRequest.setContent( site );
    }

    @Test
    public void testInstructionMacro()
        throws Exception
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create().body(
            ctx.getName() + ": param1=" + ctx.getParam( "param1" ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorScriptFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    @Test
    public void testInstructionSystemMacro()
        throws Exception
    {
        MacroKey key = MacroKey.from( ApplicationKey.SYSTEM, "mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create().body(
            ctx.getName() + ": param1=" + ctx.getParam( "param1" ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorScriptFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    @Test(expected = RenderException.class)
    public void testInstructionMissingMacro()
        throws Exception
    {
        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    private Site createSite( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapp" ) ).
            config( new PropertyTree() ).build();
        new SiteConfigsDataSerializer().toProperties( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        return Site.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            data( rootDataSet ).
            build();
    }

}