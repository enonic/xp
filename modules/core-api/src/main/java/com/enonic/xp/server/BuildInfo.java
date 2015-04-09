package com.enonic.xp.server;

import com.google.common.annotations.Beta;

@Beta
public interface BuildInfo
{
    String getHash();

    String getShortHash();

    String getTimestamp();

    String getBranch();
}
