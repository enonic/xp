package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MacroInstructionTest
{
    private MacroDescriptorService macroDescriptorService;

    private MacroProcessorFactory macroProcessorFactory;

    private MacroInstruction macroInstruction;

    private PortalRequest portalRequest;

    @BeforeEach
    void setUp()
    {
        macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        macroProcessorFactory = Mockito.mock( MacroProcessorFactory.class );

        macroInstruction = new MacroInstruction();
        macroInstruction.setMacroDescriptorService( macroDescriptorService );
        macroInstruction.setMacroProcessorFactory( macroProcessorFactory );

        portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    void testInstructionMacro()
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create()
            .body( ctx.getName() + ": params=" + ctx.getParameters() + ", body=" + ctx.getBody() )
            .build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        assertEquals( "mymacro: params={param1=[value1]}, body=body", macroInstruction.evaluate( portalRequest,
                                                                                               "MACRO _name=\"mymacro\" param1=\"value1\" _descriptor=\"myapp:mymacro\" _body=\"body\"" )
            .getBody() );
    }

    @Test
    void testNoMacroInstruction()
    {
        PortalResponse response =
            macroInstruction.evaluate( portalRequest, "MY_INSTRUCTION _name=\"mymacro\" param1=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    void testInvalidMacroInstruction()
    {
        PortalResponse response =
            macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param.with.dot=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    void testMacroInstructionWithoutName()
    {
        PortalResponse response = macroInstruction.evaluate( portalRequest, "MACRO param1=\"value1\" _body=\"body\"" );
        assertNull( response );
    }

    @Test
    void testMacroInstructionWithoutDescriptor()
    {
        MacroKey key = MacroKey.from( ApplicationKey.SYSTEM, "mymacro" );
        when( macroDescriptorService.getByKey( key ) ).thenReturn( MacroDescriptor.create().key( key ).build() );

        String outputHtml =
            (String) macroInstruction.evaluate( portalRequest, "MACRO _name=\"mymacro\" param1=\"value1\" _body=\"body\"" ).getBody();
        assertEquals( "[mymacro param1=\"value1\"]body[/mymacro]", outputHtml );
        verifyNoInteractions( macroProcessorFactory );
    }

    @Test
    void testMacroInstructionUnknownDescriptor()
    {
        String outputHtml = (String) macroInstruction.evaluate( portalRequest,
                                                                "MACRO _name=\"mymacro\" param1=\"value1\" _descriptor=\"myapp:mymacro\" _body=\"body\"" )
            .getBody();
        assertEquals( "[mymacro param1=\"value1\"]body[/mymacro]", outputHtml );
    }

    @Test
    void testMacroInstructionInvalidDescriptor()
    {
        String outputHtml = (String) macroInstruction.evaluate( portalRequest,
                                                                "MACRO _name=\"mymacro\" _descriptor=\"mymacro\" _body=\"\"" )
            .getBody();
        assertEquals( "[mymacro/]", outputHtml );
    }

    @Test
    void testMacroInstructionMissingController()
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        final RenderException e = assertThrows( RenderException.class, () -> macroInstruction.evaluate( portalRequest,
                                                                                                        "MACRO _name=\"mymacro\" param1=\"value1\" _descriptor=\"myapp:mymacro\" _body=\"body\"" ) );
        assertEquals( "Macro controller not found: mymacro", e.getMessage() );
    }

    @Test
    void testInstructionSystemMacro()
    {
        MacroKey key = MacroKey.from( ApplicationKey.SYSTEM, "mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create()
            .body( ctx.getName() + ": param1=" + ctx.getParameter( "param1" ) + ", body=" + ctx.getBody() )
            .build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        String outputHtml = (String) macroInstruction.evaluate( portalRequest,
                                                                "MACRO _name=\"mymacro\" param1=\"value1\" _descriptor=\"system:mymacro\" _body=\"body\"" )
            .getBody();
        assertEquals( "mymacro: param1=[value1], body=body", outputHtml );
    }

    @Test
    void testInstructionMacroParamsCaseInsensitive()
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        Form form = Form.create()
            .addFormItem( createTextLineInput( "param1", "Param 1" ).occurrences( 1, 1 ).build() )
            .addFormItem( createTextLineInput( "param2", "Param 2" ).occurrences( 1, 1 ).build() )
            .build();
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).form( form ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create()
            .body( ctx.getName() + ": param1=" + ctx.getParameter( "param1" ) + ", body=" + ctx.getBody() )
            .build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        assertEquals( "mymacro: param1=[value1], body=body", macroInstruction.evaluate( portalRequest,
                                                                                        "MACRO _name=\"MYMACRO\" PARAM1=\"value1\" _descriptor=\"myapp:mymacro\" _body=\"body\"" )
            .getBody() );
    }

    @Test
    void testInstructionMacroMultiValue()
    {
        MacroKey key = MacroKey.from( "myapp:mymacro" );
        MacroDescriptor macroDescriptor = MacroDescriptor.create().key( key ).build();
        when( macroDescriptorService.getByKey( key ) ).thenReturn( macroDescriptor );

        MacroProcessor macro = ( ctx ) -> PortalResponse.create()
            .body( ctx.getName() + ": param1=" + ctx.getParameter( "param1" ) + ", body=" + ctx.getBody() )
            .build();
        when( macroProcessorFactory.fromScript( any() ) ).thenReturn( macro );

        assertEquals( "mymacro: param1=[value1, value2], body=body", macroInstruction.evaluate( portalRequest,
                                                                                                "MACRO _name=\"mymacro\" param1=\"value1\" param1=\"value2\" param2=\"other\" _descriptor=\"myapp:mymacro\" _body=\"body\"" )
            .getBody() );
    }

    private Input.Builder createTextLineInput( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.TEXT_LINE ).label( label ).name( name );
    }
}
