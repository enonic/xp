package com.enonic.xp.portal.impl.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Raw (unescaped) segments of a media API path: {@code <context>/<id>[:<hash>]/[<scale>/]<name>}.
 */
@NullMarked
record MediaPathParts(String context, String id, @Nullable String hash, @Nullable String scale, String name)
{
    String idWithHash()
    {
        return hash != null ? id + ":" + hash : id;
    }
}
