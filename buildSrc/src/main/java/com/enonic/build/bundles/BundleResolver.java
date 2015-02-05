package com.enonic.build.bundles;

import java.util.List;
import java.util.stream.Collectors;

import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;

final class BundleResolver
{
    private final BundlesExtension ext;

    public BundleResolver( final BundlesExtension ext )
    {
        this.ext = ext;
    }

    public List<ResolvedBundleInfo> resolve()
    {
        this.ext.getConfiguration().resolve();
        final ResolvedConfiguration config = this.ext.getConfiguration().getResolvedConfiguration();
        return config.getResolvedArtifacts().stream().map( this::resolve ).collect( Collectors.toList() );
    }

    private ResolvedBundleInfo resolve( final ResolvedArtifact artifact )
    {
        final BundleInfo info = this.ext.getInfo( artifact );
        return new ResolvedBundleInfo( info, artifact );
    }
}
