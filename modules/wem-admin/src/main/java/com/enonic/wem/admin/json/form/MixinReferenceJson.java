package com.enonic.wem.admin.json.form;

import com.enonic.wem.api.form.MixinReference;

@SuppressWarnings("UnusedDeclaration")
public class MixinReferenceJson
    extends FormItemJson
{
    private final MixinReference mixinReference;

    public MixinReferenceJson( final MixinReference mixinReference )
    {
        super( mixinReference );
        this.mixinReference = mixinReference;
    }

    public String getReference()
    {
        return mixinReference.getMixinName().toString();
    }
}
