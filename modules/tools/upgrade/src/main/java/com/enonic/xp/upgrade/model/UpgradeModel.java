package com.enonic.xp.upgrade.model;

import java.nio.file.Path;

import com.google.common.io.CharSource;

public interface UpgradeModel
{
    boolean supports( final Path path, final String repositoryName, final String branchName );

    String upgrade( final Path path, final CharSource source );

    void log();
}
