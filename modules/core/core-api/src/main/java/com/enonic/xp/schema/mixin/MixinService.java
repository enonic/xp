package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;

@Beta
public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixins getByNames( MixinNames names );

    Mixins getAll();

    Mixins getByApplication( ApplicationKey applicationKey );

    Form inlineFormItems( Form form );
}
