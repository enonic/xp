package com.enonic.xp.portal.impl.postprocess.instruction;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.macro.MacroParamNames;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorFactory;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

@Component(immediate = true)
public final class MacroInstruction
    implements PostProcessInstruction
{
    private static final String MACRO_BODY = "_body";

    private static final String MACRO_NAME = "_name";

    public static final String MACRO_DOCUMENT = "_document";

    /**
     * The descriptor the macro was resolved to when its instruction was written, which post-processing executes.
     */
    public static final String MACRO_DESCRIPTOR = "_descriptor";

    private MacroProcessorFactory macroProcessorFactory;

    private MacroDescriptorService macroDescriptorService;

    @Override
    public PortalResponse evaluate( final PortalRequest portalRequest, final String instruction )
    {
        if ( !Instruction.isInstruction( instruction, "MACRO" ) )
        {
            return null;
        }

        // parse instruction
        final Instruction macroInstruction;
        try
        {
            macroInstruction = new InstructionParser().parse( instruction );
        }
        catch ( RenderException e )
        {
            return null;
        }

        final String macroName = macroInstruction.attribute( MACRO_NAME );
        if ( macroName == null )
        {
            return null;
        }

        // the descriptor was resolved when the instruction was written, for the site or project of the text;
        // an instruction naming none is left as the macro it stands for
        final MacroDescriptor macroDescriptor = resolveMacroDescriptor( macroInstruction.attribute( MACRO_DESCRIPTOR ) );
        if ( macroDescriptor == null )
        {
            return PortalResponse.create().body( toMacroInstruction( macroInstruction ) ).build();
        }

        final MacroProcessor macroProcessor = resolveMacroProcessor( macroDescriptor );
        if ( macroProcessor == null )
        {
            throw new RenderException( "Macro controller not found: " + macroName );
        }

        // execute macro
        final MacroContext context = createContext( macroInstruction, macroDescriptor, portalRequest );
        return macroProcessor.process( context );
    }

    private MacroDescriptor resolveMacroDescriptor( final String descriptorKey )
    {
        if ( descriptorKey == null )
        {
            return null;
        }
        try
        {
            return macroDescriptorService.getByKey( MacroKey.from( descriptorKey ) );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

    private MacroProcessor resolveMacroProcessor( MacroDescriptor macroDescriptor )
    {
        if ( macroDescriptor != null )
        {
            return macroProcessorFactory.fromScript( macroDescriptorService.getControllerResourceKey( macroDescriptor.getKey() ) );
        }
        return null;
    }

    private MacroContext createContext( final Instruction macroInstruction, final MacroDescriptor macroDescriptor,
                                        final PortalRequest request )
    {
        final MacroParamNames paramNames = new MacroParamNames( macroDescriptor );

        final MacroContext.Builder context = MacroContext.create().name( macroDescriptor.getName() );
        for ( String name : macroInstruction.attributeNames() )
        {
            if ( name.equalsIgnoreCase( MACRO_BODY ) || name.equalsIgnoreCase( MACRO_NAME ) || name.equalsIgnoreCase( MACRO_DOCUMENT ) ||
                name.equalsIgnoreCase( MACRO_DESCRIPTOR ) )
            {
                continue;
            }

            final String contextParamName = paramNames.of( name );
            for ( String attribute : macroInstruction.attributes( name ) )
            {
                context.param( contextParamName, attribute );
            }
        }
        context.body( macroInstruction.attribute( MACRO_BODY ) );
        context.request( request );
        final String documentRef = macroInstruction.attribute( MACRO_DOCUMENT );
        final String document = (String) ContextAccessor.current().getLocalScope().getAttribute( documentRef );
        context.document( document );
        return context.build();
    }

    private String toMacroInstruction( final Instruction macroInstruction )
    {
        final Macro.Builder macro = Macro.create().name( macroInstruction.attribute( MACRO_NAME ) );
        for ( String name : macroInstruction.attributeNames() )
        {
            if ( name.equalsIgnoreCase( MACRO_BODY ) || name.equalsIgnoreCase( MACRO_NAME ) || name.equalsIgnoreCase( MACRO_DOCUMENT ) ||
                name.equalsIgnoreCase( MACRO_DESCRIPTOR ) )
            {
                continue;
            }
            for ( String attribute : macroInstruction.attributes( name ) )
            {
                macro.param( name, attribute );
            }
        }
        macro.body( macroInstruction.attribute( MACRO_BODY ) );
        return macro.build().toString();
    }

    @Reference
    public void setMacroProcessorFactory( final MacroProcessorFactory macroProcessorFactory )
    {
        this.macroProcessorFactory = macroProcessorFactory;
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }
}
