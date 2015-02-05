package com.enonic.build.bundles;

import org.gradle.api.artifacts.ResolvedArtifact;

final class ResolvedBundleInfo
{
    private final BundleInfo info;

    private final ResolvedArtifact artifact;

    public ResolvedBundleInfo( final BundleInfo info, final ResolvedArtifact artifact )
    {
        this.info = info;
        this.artifact = artifact;
    }

    public int getLevel()
    {
        return this.info.getLevel();
    }

    public ResolvedArtifact getArtifact()
    {
        return this.artifact;
    }
}
