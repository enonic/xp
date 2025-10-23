package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.GenericValue;

final class InputTypeHelper
{
    private InputTypeHelper()
    {
        throw new AssertionError();
    }

    public static void populateOptions( final List<OptionYml> options, final Input.Builder builder )
    {
        if ( options == null )
        {
            return;
        }

        final GenericValue.ListBuilder optionList = GenericValue.list();

        options.forEach( option -> {
            final GenericValue.ObjectBuilder optionBuilder = GenericValue.object().put( "value", option.value );

            if ( option.label != null )
            {
                final GenericValue.ObjectBuilder labelBuilder = GenericValue.object().put( "text", option.label.text() );
                if ( option.label.i18n() != null )
                {
                    labelBuilder.put( "i18n", option.label.i18n() );
                }
                optionBuilder.put( "label", labelBuilder.build() );
            }

            option.getAttributes().forEach( optionBuilder::put );

            optionList.add( optionBuilder.build() );
        } );

        builder.inputTypeProperty( "option", optionList.build() );
    }
}
