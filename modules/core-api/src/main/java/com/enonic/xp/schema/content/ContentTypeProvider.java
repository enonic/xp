package com.enonic.xp.schema.content;

import java.util.function.Supplier;

import com.google.common.annotations.Beta;

@Beta
public interface ContentTypeProvider
    extends Supplier<ContentTypes>
{
}
