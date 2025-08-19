package com.enonic.xp.core.impl.schema.mapper.sandbox;

import java.util.List;

import com.enonic.xp.form.FormItemSet;

public class ItemSetYml
    extends FormItemYml
{
    public String name;

    public I18nYml label;

    public boolean immutable;

    public OccurrencesYml occurrences;

    public String customText;

    public I18nYml helpText;

    public List<FormItemYml> items;

    public FormItemSet toFormItemSet()
    {
        final FormItemSet.Builder formItemSet = FormItemSet.create().name( name );

        if ( label != null )
        {
            formItemSet.label( label.text );
            formItemSet.labelI18nKey( label.i18n );
        }

        if ( helpText != null )
        {
            formItemSet.helpText( helpText.text );
            formItemSet.helpTextI18nKey( helpText.i18n );
        }

        formItemSet.immutable( immutable );

        if ( occurrences != null )
        {
            formItemSet.occurrences( occurrences.minimum, occurrences.maximum );
        }

        formItemSet.customText( customText );

        items.forEach( i -> {
            if ( i instanceof InputYml input )
            {
                formItemSet.addFormItem( InputRegistry.toInput( input ) );
            }
        } );

        return formItemSet.build();
    }
}
