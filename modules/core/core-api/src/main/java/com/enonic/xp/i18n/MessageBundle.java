package com.enonic.xp.i18n;

import java.util.Set;

public interface MessageBundle
{
    Set<String> getKeys();

    String localize( String key, Object... args );
}
