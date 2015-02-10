package com.enonic.wem.api.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("UnusedDeclaration")
public class InlineJson
    extends FormItemJson
{
    private final Inline inline;

    public InlineJson( final Inline inline )
    {
        this.inline = inline;
    }

    @JsonIgnore
    @Override
    public FormItem getFormItem()
    {
        return inline;
    }

    @Override
    public String getName()
    {
        return inline.getName();
    }

    public String getReference()
    {
        return inline.getMixinName().toString();
    }
}
