package com.enonic.xp.impl.macro;


import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

public class BuiltinMacroDescriptors
{
    private final MacroDescriptors macroDescriptors = generateMacroDescriptors();

    public MacroDescriptor getByKey( final MacroKey key )
    {
        return macroDescriptors.stream().
            filter( macroDescriptor -> macroDescriptor.getKey().equals( key ) ).
            findFirst().
            orElse( null );
    }

    public MacroDescriptors getAll()
    {
        return macroDescriptors;
    }

    private MacroDescriptors generateMacroDescriptors()
    {
        final ImmutableSet.Builder<MacroDescriptor> macroDescriptors = ImmutableSet.builder();
        macroDescriptors.add( generateYouTrackMacroDescriptor() ).
            add( generateTwitterMacroDescriptor() ).
            add( generateEmbeddedCodeMacroDescriptor() ).
            add( generateNoFormatMacroDescriptor() );

        return MacroDescriptors.from( macroDescriptors.build() );
    }

    private MacroDescriptor generateYouTrackMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "youtube" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            addFormItem( createTextLineInput( "width", "Width" ).occurrences( 0, 1 ).build() ).
            addFormItem( createTextLineInput( "height", "Height" ).occurrences( 0, 1 ).build() ).
            build();

        return MacroDescriptor.create().
            key( macroKey ).
            displayName( "Youtube macro" ).
            description( "Youtube macro" ).
            form( form ).
            build();
    }

    private MacroDescriptor generateTwitterMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "tweet" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            addFormItem( createTextLineInput( "lang", "Language" ).occurrences( 0, 1 ).build() ).
            build();

        return MacroDescriptor.create().
            key( macroKey ).
            displayName( "Twitter macro" ).
            description( "Twitter macro" ).
            form( form ).
            build();
    }

    private MacroDescriptor generateEmbeddedCodeMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "code" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Body" ).occurrences( 1, 1 ).build() ).
            build();

        return MacroDescriptor.create().
            key( macroKey ).
            displayName( "Embedded code macro" ).
            description( "Embedded code macro" ).
            form( form ).
            build();
    }

    private MacroDescriptor generateNoFormatMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "noformat" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Body" ).occurrences( 1, 1 ).build() ).
            build();

        return MacroDescriptor.create().
            key( macroKey ).
            displayName( "No Format macro" ).
            description( "The contents of the body will not be formatted" ).
            form( form ).
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
