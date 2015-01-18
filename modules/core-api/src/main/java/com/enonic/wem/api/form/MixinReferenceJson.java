package com.enonic.wem.api.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("UnusedDeclaration")
public class MixinReferenceJson
    extends FormItemJson
{
    private final MixinReference mixinReference;

    public MixinReferenceJson( final MixinReference mixinReference )
    {
        this.mixinReference = mixinReference;
    }

    @JsonIgnore
    @Override
    public FormItem getFormItem()
    {
        return mixinReference;
    }

    @Override
    public String getName()
    {
        return mixinReference.getName();
    }

    public String getReference()
    {
        return mixinReference.getMixinName().toString();
    }
}
