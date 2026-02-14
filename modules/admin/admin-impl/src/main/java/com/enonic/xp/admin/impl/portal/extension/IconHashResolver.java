package com.enonic.xp.admin.impl.portal.extension;

import java.util.Arrays;
import java.util.Base64;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.icon.Icon;

public final class IconHashResolver
{
    private IconHashResolver()
    {
    }

    public static String resolve( final Icon icon )
    {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString( Arrays.copyOf( MessageDigests.sha512().digest( icon.toByteArray() ), 16 ) );
    }
}
