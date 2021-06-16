package com.enonic.xp.portal.impl.url;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.impl.postprocess.instruction.MacroInstruction;

public final class HtmlMacroProcessor
{
    private static final String MACRO_DOCUMENT_COUNTER = "__macroDocumentCounter";

    private static final String MACRO_DOCUMENT_REF_PREFIX = "__macroDocument";

    private final MacroService macroService;

    public HtmlMacroProcessor( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    public String process( final String text )
    {
        final LocalScope localScope = ContextAccessor.current().getLocalScope();
        return macroService.evaluateMacros( text, ( macro ) -> {

            Integer macroDocCounter = (Integer) localScope.getAttribute( MACRO_DOCUMENT_COUNTER );
            macroDocCounter = macroDocCounter == null ? 1 : macroDocCounter + 1;
            final String documentRef = MACRO_DOCUMENT_REF_PREFIX + macroDocCounter;
            localScope.setAttribute( documentRef, text );
            localScope.setAttribute( MACRO_DOCUMENT_COUNTER, macroDocCounter );

            macro = Macro.copyOf( macro ).param( MacroInstruction.MACRO_DOCUMENT, documentRef ).build();
            return macroService.postProcessInstructionSerialize( macro );
        } );
    }

}
