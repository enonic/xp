package com.enonic.xp.admin.impl.portal.extension;

import com.google.common.hash.Hashing;

import com.enonic.xp.icon.Icon;

public final class IconHashResolver
{
    private IconHashResolver()
    {
    }

    public static String resolve( final Icon icon )
    {
        return Hashing.md5().hashBytes( icon.toByteArray() ).toString();
    }
}
