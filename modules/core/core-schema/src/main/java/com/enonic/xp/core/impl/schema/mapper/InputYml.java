package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.LocalizedText;

public abstract class InputYml
    extends FormItem
{
    public String type;

    public String name;

    public LocalizedText label;

    public LocalizedText helpText;

    public Occurrences occurrences;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.INPUT;
    }

    @Override
    public FormItem copy()
    {
        final Input.Builder builder = Input.create().name( name ).inputType( getInputTypeName() );

        if ( label != null )
        {
            builder.label( label.text() ).labelI18nKey( label.i18n() );
        }

        if ( helpText != null )
        {
            builder.helpText( helpText.text() ).helpTextI18nKey( helpText.i18n() );
        }

        if ( occurrences != null )
        {
            builder.occurrences( occurrences );
        }

        customizeInputType( builder );

        return builder.build();
    }

    public abstract InputTypeName getInputTypeName();

    public abstract void customizeInputType( Input.Builder builder );
}
