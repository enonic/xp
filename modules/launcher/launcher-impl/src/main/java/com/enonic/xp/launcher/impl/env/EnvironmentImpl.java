package com.enonic.xp.launcher.impl.env;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.launcher.impl.SharedConstants;

final class EnvironmentImpl
    implements Environment
{
    private final Path homeDir;

    private final Path installDir;

    EnvironmentImpl( final Path installDir, final Path homeDir )
    {
        this.homeDir = Objects.requireNonNull( homeDir, String.format( "Home directory not set. Please set [%s] system property variable.",
                                                                       SharedConstants.XP_HOME_DIR ) ).toAbsolutePath().normalize();
        this.installDir = Objects.requireNonNull( installDir,
                                                  String.format( "Install directory not set. Please set [%s] system property variable.",
                                                                 SharedConstants.XP_INSTALL_DIR ) ).toAbsolutePath().normalize();
    }

    @Override
    public Path getHomeDir()
    {
        return this.homeDir;
    }

    @Override
    public Path getInstallDir()
    {
        return this.installDir;
    }

    @Override
    public Map<String, String> getAsMap()
    {
        return Map.of( SharedConstants.XP_HOME_DIR, this.homeDir.toString(), SharedConstants.XP_INSTALL_DIR, this.installDir.toString() );
    }
}
