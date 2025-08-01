package com.enonic.xp.schema.mixin;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;

@PublicApi
public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixins getAll();

    Mixins getByApplication( ApplicationKey applicationKey );

    Form inlineFormItems( Form form );
}
