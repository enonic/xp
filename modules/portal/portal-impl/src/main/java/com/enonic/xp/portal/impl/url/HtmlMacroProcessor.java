package com.enonic.xp.portal.impl.url;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.impl.macro.MacroDescriptorResolver;
import com.enonic.xp.portal.impl.postprocess.instruction.MacroInstruction;

/**
 * Turns the macros of rich text into instructions that post-processing executes. Each macro is resolved here, among
 * the applications of the site or project the text belongs to, and its instruction names the resolved descriptor; a
 * macro no application provides is left as written.
 */
public final class HtmlMacroProcessor
{
    private static final String MACRO_DOCUMENT_COUNTER = "__macroDocumentCounter";

    private static final String MACRO_DOCUMENT_REF_PREFIX = "__macroDocument";

    private final MacroService macroService;

    private final MacroDescriptorResolver macroDescriptorResolver;

    private final ApplicationKeys applications;

    /**
     * @param applications the applications of the site or project the text belongs to, which macros come from
     */
    public HtmlMacroProcessor( final MacroService macroService, final MacroDescriptorResolver macroDescriptorResolver,
                               final ApplicationKeys applications )
    {
        this.macroService = macroService;
        this.macroDescriptorResolver = macroDescriptorResolver;
        this.applications = applications;
    }

    public String process( final String text )
    {
        final String safeText = withoutInstructions( text );

        final LocalScope localScope = ContextAccessor.current().getLocalScope();
        return macroService.evaluateMacros( safeText, ( macro ) -> {
            final MacroDescriptor descriptor = macroDescriptorResolver.resolve( applications, macro.getName() );
            if ( descriptor == null )
            {
                return macro.toString();
            }

            Integer macroDocCounter = (Integer) localScope.getAttribute( MACRO_DOCUMENT_COUNTER );
            macroDocCounter = macroDocCounter == null ? 1 : macroDocCounter + 1;
            final String documentRef = MACRO_DOCUMENT_REF_PREFIX + macroDocCounter;
            localScope.setAttribute( documentRef, safeText );
            localScope.setAttribute( MACRO_DOCUMENT_COUNTER, macroDocCounter );

            final Macro instruction = Macro.copyOf( macro )
                .param( MacroInstruction.MACRO_DESCRIPTOR, descriptor.getKey().toString() )
                .param( MacroInstruction.MACRO_DOCUMENT, documentRef )
                .build();
            return macroService.postProcessInstructionSerialize( instruction );
        } );
    }

    /**
     * Turns the post-processing instructions written in rich text into plain comments, so that only the instructions
     * written for its macros are executed.
     */
    public static String withoutInstructions( final String text )
    {
        return text.replace( "<!--#", "<!-- #" );
    }
}
