package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorFactory;
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

    private MacroProcessorFactory macroProcessorFactory;

    private MacroInstruction macroInstruction;

    private PortalRequest portalRequest;

    @Before
    public void setUp()
    {
        macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        macroProcessorFactory = Mockito.mock( MacroProcessorFactory.class );

        macroInstruction = new MacroInstruction();
        macroInstruction.setMacroDescriptorService( macroDescriptorService );
        macroInstruction.setMacroProcessorFactory( macroProcessorFactory );

        portalRequest = new PortalRequest();
        Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
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
            ctx.getName() + ": param1=" + ctx.getParameter( "param1" ).get( 0 ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    @Test
    public void testNoMacroInstruction()
        throws Exception
    {
        PortalResponse response =
            macroInstruction.evaluate( portalRequest, "MY_INSTRUCTION _name=\"mymacro\" param1=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    public void testInvalidMacroInstruction()
        throws Exception
    {
        PortalResponse response =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param.with.dot=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    public void testMacroInstructionWithoutName()
        throws Exception
    {
        PortalResponse response = macroInstruction.evaluate( portalRequest, "MACRO param1=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    public void testMacroInstructionNotSiteContext()
        throws Exception
    {
        portalRequest.setSite( null );
        try
        {
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" );
            fail( "Expected exception" );
        }
        catch ( RenderException e )
        {
            assertEquals( "Macro controller script could not be resolved, context site could not be found.", e.getMessage() );
        }
    }

    @Test
    public void testMacroInstructionMissingController()
        throws Exception
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        try
        {
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" );

            fail( "Expected exception" );
        }
        catch ( RenderException e )
        {
            assertEquals( "Macro controller not found: mymacro", e.getMessage() );
        }
    }

    @Test
    public void testInstructionSystemMacro()
        throws Exception
    {
        MacroKey key = MacroKey.from( ApplicationKey.SYSTEM, "mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );
        when( macroDescriptorService.getByApplication( any() ) ).thenReturn( MacroDescriptors.empty() );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create().body(
            ctx.getName() + ": param1=" + ctx.getParam( "param1" ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    @Test
    public void testInstructionMissingMacro()
        throws Exception
    {
        MacroKey key = MacroKey.from( "myapp:somemacro" );
        Form form = Form.create().build();
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).form( form ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );
        when( macroDescriptorService.getByApplication( key.getApplicationKey() ) ).thenReturn( MacroDescriptors.from( macroDescriptor ) );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "[mymacro param1=\"value1\"]body[/mymacro]", outputHtml );
    }

    @Test
    public void testInstructionMacroParamsCaseInsensitive()
        throws Exception
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        Form form = Form.create().
            addFormItem( createTextLineInput( "param1", "Param 1" ).occurrences( 1, 1 ).build() ).
            addFormItem( createTextLineInput( "param2", "Param 2" ).occurrences( 1, 1 ).build() ).

            build();
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).form( form ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );
        when( macroDescriptorService.getByApplication( key.getApplicationKey() ) ).thenReturn( MacroDescriptors.from( macroDescriptor ) );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create().body(
            ctx.getName() + ": param1=" + ctx.getParam( "param1" ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"MYMACRO\" PARAM1=\"value1\" _body=\"body\"" ).getAsString();
        assertEquals( "mymacro: param1=value1, body=body", outputHtml );
    }

    @Test
    public void testInstructionMacroMultiValue()
        throws Exception
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create().body(
            ctx.getName() + ": param1=" + ctx.getParameter( "param1" ).get( 0 ) + ", body=" + ctx.getBody() ).build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml = macroInstruction.evaluate( portalRequest,
                                                       "MACRO _name=\"mymacro\" param1=\"value1\" param1=\"value2\" param2=\"other\" _body=\"body\"" ).getAsString();
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

    private Input.Builder createTextLineInput( final String name, final String label )
    {
        return Input.create().
            inputType( InputTypeName.TEXT_LINE ).
            label( label ).
            name( name ).
            immutable( true );
    }
}