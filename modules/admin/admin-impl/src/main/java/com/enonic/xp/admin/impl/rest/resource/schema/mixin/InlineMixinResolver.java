package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinService;

public final class InlineMixinResolver
{
    private MixinService mixinService;

    public InlineMixinResolver( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    public Form inlineForm( final Form form )
    {
        return this.mixinService.inlineFormItems( form );
    }
}
