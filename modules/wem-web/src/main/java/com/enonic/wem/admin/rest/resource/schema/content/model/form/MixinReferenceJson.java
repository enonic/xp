package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.api.schema.content.form.MixinReference;

public class MixinReferenceJson
    extends FormItemJson
{
    private final MixinReference mixinReference;

    public MixinReferenceJson( final MixinReference mixinReference )
    {
        this.mixinReference = mixinReference;
    }

    public String getName()
    {
        return mixinReference.getName();
    }

    public String getType()
    {
        return mixinReference.getMixinClass().getSimpleName();
    }

    public String getReference()
    {
        return mixinReference.getQualifiedMixinName().toString();
    }
}
