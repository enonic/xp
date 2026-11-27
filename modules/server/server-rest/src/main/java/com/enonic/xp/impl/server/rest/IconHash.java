package com.enonic.xp.impl.server.rest;

import java.util.Arrays;
import java.util.Base64;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.icon.Icon;

@NullMarked
public final class IconHash
{
    private static final int LENGTH = 16;

    private IconHash()
    {
    }

    public static String of( final Icon icon )
    {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString( Arrays.copyOf( MessageDigests.sha512().digest( icon.toByteArray() ), LENGTH ) );
    }
}
