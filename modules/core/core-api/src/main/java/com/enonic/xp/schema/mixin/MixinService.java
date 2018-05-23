package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;

@Beta
public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixins getByNames( MixinNames names );

    Mixins getAll();

    Mixins getByApplication( ApplicationKey applicationKey );

    Mixins getByContentType( ContentType contentType );

    Form inlineFormItems( Form form );
}
