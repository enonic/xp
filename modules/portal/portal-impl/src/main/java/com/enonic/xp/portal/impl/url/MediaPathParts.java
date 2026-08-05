package com.enonic.xp.portal.impl.url;

/**
 * Raw (unescaped) segments of a media API path: {@code <context>/<id>[:<hash>]/[<scale>/]<name>}.
 */
record MediaPathParts(String context, String id, String hash, String scale, String name)
{
    String idWithHash()
    {
        return hash != null ? id + ":" + hash : id;
    }
}
