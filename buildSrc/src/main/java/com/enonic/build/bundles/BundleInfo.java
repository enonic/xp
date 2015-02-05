package com.enonic.build.bundles;

import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;

public final class BundleInfo
{
    private final String id;

    private final Dependency dependency;

    private final int level;

    public BundleInfo( final Dependency dependency, final int level )
    {
        this.dependency = dependency;
        this.level = level;
        this.id = toId( this.dependency );
    }

    public String getId()
    {
        return this.id;
    }

    public int getLevel()
    {
        return this.level;
    }

    @Override
    public String toString()
    {
        return this.dependency.toString() + "-" + this.level;
    }

    public static String toId( final Dependency item )
    {
        return item.getGroup() + ":" + item.getName() + ":" + item.getVersion();
    }

    public static String toId( final ResolvedArtifact item )
    {
        final ModuleVersionIdentifier id = item.getModuleVersion().getId();
        return id.getGroup() + ":" + id.getName() + ":" + id.getVersion();
    }
}
