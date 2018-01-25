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
        Arrays.asList( generateDisableMacroDescriptor(), generateEmbedIFrameMacroDescriptor() ).stream().
            forEach( ( md ) -> macroDescriptors.put( md.getName().toLowerCase(), md ) );

        macrosByName = macroDescriptors.build();
        return MacroDescriptors.from( macrosByName.values() );
    }

    private MacroDescriptor generateDisableMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "disable" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Contents", macroKey ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Disable macros", "Contents of this macro will not be formatted", form );
    }

    private MacroDescriptor generateEmbedIFrameMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "embed" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "IFrame HTML", macroKey ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Embed IFrame", "Generic iframe embedder", form );
    }

    private MacroDescriptor create( final MacroKey macroKey, final String displayName, final String description, final Form form )
    {
        return MacroDescriptor.create().
            key( macroKey ).
            displayName( displayName ).
            displayNameI18nKey( macroKey.getApplicationKey().getName() + "." + macroKey.getName() + ".displayName" ).
            description( description ).
            descriptionI18nKey( macroKey.getApplicationKey().getName() + "." + macroKey.getName() + ".description" ).
            form( form ).
            icon( IconLoader.loadIcon( this.getClass(), MACRO_DESCRIPTORS_FOLDER, macroKey.getName() ) ).
            build();
    }

    private static Input.Builder createTextAreaInput( final String name, final String label, final MacroKey macroKey )
    {
        return Input.create().
            inputType( InputTypeName.TEXT_AREA ).
            label( label ).
            labelI18nKey( macroKey.getApplicationKey().getName() + "." + macroKey.getName() + "." + name + ".label" ).
            name( name ).
            immutable( true );
    }
}
