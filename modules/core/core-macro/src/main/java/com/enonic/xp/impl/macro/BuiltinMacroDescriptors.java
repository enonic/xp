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

    private static final String MACRO_DESCRIPTORS_FOLDER = "macro-descriptors";

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
        macroDescriptors.add( generateYoutubeMacroDescriptor() ).
            add( generateTwitterMacroDescriptor() ).
            add( generateEmbeddedCodeMacroDescriptor() ).
            add( generateNoFormatMacroDescriptor() );

        return MacroDescriptors.from( macroDescriptors.build() );
    }

    private MacroDescriptor generateYoutubeMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "youtube" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Youtube macro", "Youtube macro", form );
    }

    private MacroDescriptor generateTwitterMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "tweet" );
        final Form form = Form.create().
            addFormItem( createTextLineInput( "url", "Url" ).occurrences( 1, 1 ).build() ).
            addFormItem( createTextLineInput( "lang", "Language" ).occurrences( 0, 1 ).build() ).
            build();

        return create( macroKey, "Twitter macro", "Twitter macro", form );
    }

    private MacroDescriptor generateEmbeddedCodeMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "code" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Code" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "Embedded code macro", "Embedded code macro", form );
    }

    private MacroDescriptor generateNoFormatMacroDescriptor()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "noformat" );

        final Form form = Form.create().
            addFormItem( createTextAreaInput( "body", "Contents" ).occurrences( 1, 1 ).build() ).
            build();

        return create( macroKey, "No Format macro", "The contents of the body will not be formatted", form );
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
