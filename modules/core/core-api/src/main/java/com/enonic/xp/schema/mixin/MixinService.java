package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNameWildcardResolver;

@Beta
public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixins getByNames( MixinNames names );

    Mixins getAll();

    Mixins getByApplication( ApplicationKey applicationKey );

    Mixins getByContentType( ContentType contentType );

    Mixins filterMixinsByContentType( final MixinNames mixinNames, final ContentTypeName contentTypeName,
                                      final ContentTypeNameWildcardResolver contentTypeNameWildcardResolver );

    Form inlineFormItems( Form form );
}
