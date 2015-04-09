package com.enonic.xp.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class InlineMixinJson
    extends FormItemJson
{
    private final InlineMixin inline;

    public InlineMixinJson( final InlineMixin inline )
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
