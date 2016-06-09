package com.enonic.xp.impl.macro;


import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

public final class BuiltinMacroDescriptors
{
    private final MacroDescriptors macroDescriptors = generateMacroDescriptors();

    private Map<String, MacroDescriptor> macrosByName;

    private static final String MACRO_DESCRIPTORS_FOLDER = "macro-descriptors";

    public MacroDescriptor getByKey( final MacroKey key )
    {
        if ( !ApplicationKey.SYSTEM.equals( key.getApplicationKey() ) )
        {
            return null;
        }
        return macrosByName.get( key.getName().toLowerCase() );
    }

    public MacroDescriptors getAll()
    {
        return macroDescriptors;
    }

    private MacroDescriptors generateMacroDescriptors()
    {
        final ImmutableMap.Builder<String, MacroDescriptor> macroDescriptors = ImmutableMap.builder();
        Arrays.asList( generateYoutubeMacroDescriptor(), generateTwitterMacroDescriptor(), generateEmbeddedCodeMacroDescriptor(),
                       generateNoFormatMacroDescriptor(), generateEmbedIFrameMacroDescriptor() ).stream().
            forEach( ( md ) -> macroDescriptors.put( md.getName().toLowerCase(), md ) );

        macrosByName = macroDescriptors.build();
        return MacroDescriptors.from( macrosByName.values() );
    }

    private MacroDescriptor generateYoutubeMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "youtube" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "YouTube macro", "Stream a video directly from your website", form );
    }

    private MacroDescriptor generateTwitterMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "tweet" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            addFormItem( createTextLineInput( "lang", "Language" ).occurrences( 0, 1 ).build() ).
            build();

        return create( macroKey, "Twitter macro", "Insert a single Tweet into your article or website", form );
    }

    private MacroDescriptor generateEmbeddedCodeMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "code" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Code" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Embedded code macro", "Embed a code snippet on your webpage", form );
    }

    private MacroDescriptor generateNoFormatMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "noformat" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Contents" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "No Format macro", "Contents of this macro will not be formatted", form );
    }

    private MacroDescriptor generateEmbedIFrameMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "embed" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "IFrame HTML" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Embed IFrame", "Generic iframe embedder", form );
    }

    private MacroDescriptor create( final MacroKey macroKey, final String displayName, final String description, final Form form )
    {
        return MacroDescriptor.create().
            key( macroKey ).
            displayName( displayName ).
            description( description ).
            form( form ).
            icon( IconLoader.loadIcon( this.getClass(), MACRO_DESCRIPTORS_FOLDER, macroKey.getName() ) ).
            build();
    }

    private static Input.Builder createTextLineInput( final String name, final String label )
    {
        return Input.create().
            inputType( InputTypeName.TEXT_LINE ).
            label( label ).
            name( name ).
            immutable( true );
    }

    private static Input.Builder createTextAreaInput( final String name, final String label )
    {
        return Input.create().
            inputType( InputTypeName.TEXT_AREA ).
            label( label ).
            name( name ).
            immutable( true );
    }
}
