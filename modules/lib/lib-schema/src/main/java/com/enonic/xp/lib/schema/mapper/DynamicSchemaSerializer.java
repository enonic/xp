package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.GenericValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.script.serializer.InputTypeConfigSerializer;
import com.enonic.xp.script.serializer.MapGenerator;

public class DynamicSchemaSerializer
{
    static void serializeIcon( final MapGenerator gen, final Icon icon )
    {
        if ( icon == null || icon.getSize() == 0 )
        {
            return;
        }
        gen.map( "icon" );
        gen.value( "data", new IconByteSource( icon ) );
        gen.value( "mimeType", icon.getMimeType() );
        gen.value( "modifiedTime", icon.getModifiedTime() );
        gen.end();
    }

    static void serializeForm( final MapGenerator gen, final Form form )
    {
        gen.array( "form" );
        for ( FormItem item : form )
        {
            serializeItem( gen, item );
        }
        gen.end();
    }

    static void serializeConfig( final MapGenerator gen, final GenericValue config )
    {
        InputTypeConfigSerializer.serializeConfig( gen, config );
    }

    static void serializeRegions( final MapGenerator gen, final RegionDescriptors regions )
    {
        if ( regions != null && regions.numberOfRegions() > 0 )
        {
            gen.array( "regions" );

            regions.forEach( gen::value );

            gen.end();
        }
    }

    private static void serializeItem( final MapGenerator gen, final FormItem item )
    {
        if ( item instanceof FormItemSet )
        {
            serializeFormItemSet( gen, (FormItemSet) item );
        }
        else if ( item instanceof FieldSet )
        {
            serializeLayout( gen, (FieldSet) item );
        }
        else if ( item instanceof Input )
        {
            serializeInput( gen, (Input) item );
        }
        else if ( item instanceof FormFragment )
        {
            serializeFormFragment( gen, (FormFragment) item );
        }
        else if ( item instanceof FormOptionSet )
        {
            serializeFormOptionSet( gen, (FormOptionSet) item );
        }
    }

    private static void serializeOccurrences( final MapGenerator gen, final Occurrences occurrences )
    {
        gen.map( "occurrences" );
        gen.value( "maximum", occurrences.getMaximum() );
        gen.value( "minimum", occurrences.getMinimum() );
        gen.end();
    }

    private static void serializeFormOptionSet( final MapGenerator gen, final FormOptionSet optionSet )
    {
        gen.map();
        gen.value( "formItemType", "OptionSet" );
        gen.value( "name", optionSet.getName() );
        gen.value( "label", optionSet.getLabel() );
        gen.value( "expanded", optionSet.isExpanded() );
        gen.value( "helpText", optionSet.getHelpText() );
        serializeOccurrences( gen, optionSet.getOccurrences() );
        serializeMultiselection( gen, optionSet.getMultiselection() );

        gen.array( "options" );
        for ( FormOptionSetOption option : optionSet )
        {
            serializeOption( gen, option );
        }
        gen.end();

        gen.end();
    }

    private static void serializeOption( final MapGenerator gen, final FormOptionSetOption option )
    {
        gen.map();
        gen.value( "name", option.getName() );
        gen.value( "label", option.getLabel() );
        gen.value( "helpText", option.getHelpText() );
        gen.value( "default", option.isDefaultOption() );

        gen.array( "items" );
        for ( FormItem formItem : option )
        {
            serializeItem( gen, formItem );
        }
        gen.end();

        gen.end();
    }

    private static void serializeMultiselection( final MapGenerator gen, final Occurrences multiselection )
    {
        gen.map( "selection" );
        gen.value( "maximum", multiselection.getMaximum() );
        gen.value( "minimum", multiselection.getMinimum() );
        gen.end();
    }

    private static void serializeInput( final MapGenerator gen, final Input input )
    {
        gen.map();
        gen.value( "formItemType", "Input" );
        gen.value( "name", input.getName() );
        gen.value( "label", input.getLabel() );
        gen.value( "helpText", input.getHelpText() );
        gen.value( "inputType", input.getInputType().toString() );
        serializeOccurrences( gen, input.getOccurrences() );
        serializeConfig( gen, input.getInputTypeConfig() );
        gen.end();
    }

    private static void serializeFormFragment( final MapGenerator gen, final FormFragment formFragment )
    {
        gen.map();
        gen.value( "formItemType", "FormFragment" );
        gen.value( "name", formFragment.getFormFragmentName() );
        gen.end();
    }

    private static void serializeLayout( final MapGenerator gen, final FieldSet fieldSet )
    {
        gen.map();
        gen.value( "formItemType", "Layout" );
        gen.value( "label", fieldSet.getLabel() );
        gen.array( "items" );
        for ( FormItem formItem : fieldSet )
        {
            serializeItem( gen, formItem );
        }
        gen.end();
        gen.end();
    }

    private static void serializeFormItemSet( final MapGenerator gen, final FormItemSet itemSet )
    {
        gen.map();
        gen.value( "formItemType", "ItemSet" );
        gen.value( "name", itemSet.getName() );
        gen.value( "label", itemSet.getLabel() );
        gen.value( "customText", itemSet.getCustomText() );
        gen.value( "helpText", itemSet.getHelpText() );
        serializeOccurrences( gen, itemSet.getOccurrences() );

        gen.array( "items" );
        for ( FormItem formItem : itemSet )
        {
            serializeItem( gen, formItem );
        }
        gen.end();

        gen.end();
    }
}
