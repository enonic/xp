package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.MixinReference;

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
