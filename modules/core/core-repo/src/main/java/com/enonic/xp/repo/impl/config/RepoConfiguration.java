package com.enonic.xp.repo.impl.config;

import java.nio.file.Path;

public interface RepoConfiguration
{
    Path getSnapshotsDir();

    String cacheCapacity();
}
