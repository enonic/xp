package com.enonic.xp.core.impl.security.token;

import java.nio.charset.StandardCharsets;

import com.google.common.io.BaseEncoding;

import org.jspecify.annotations.NullMarked;

/**
 * Node names for the ids of an id provider's registries. An id may hold characters a node name
 * forbids and is case-sensitive, while a node path compares without case, so a name is the id's
 * UTF-8 bytes in Base32 (RFC 4648 section 6), the encoding made for a form that needs to be case
 * insensitive but not human readable: {@code content:read} is stored as
 * {@code mnxw45dfnz2du4tfmfsa}, and {@code Read} and {@code read} get names of their own. The id
 * itself travels with the node's data.
 */
@NullMarked
final class RegistryNodeNames
{
    private static final BaseEncoding BASE32 = BaseEncoding.base32().lowerCase().omitPadding();

    private RegistryNodeNames()
    {
    }

    static String of( final String id )
    {
        return BASE32.encode( id.getBytes( StandardCharsets.UTF_8 ) );
    }
}
