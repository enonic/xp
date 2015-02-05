package com.enonic.build.bundles;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolvedArtifact;

public class BundlesExtension
{
    public final static String CONFIG_NAME = "bundle";

    private final Project project;

    private final Map<String, BundleInfo> infoMap;

    public BundlesExtension( final Project project )
    {
        this.project = project;
        this.infoMap = new HashMap<>();
    }

    public Configuration getConfiguration()
    {
        return this.project.getConfigurations().getByName( CONFIG_NAME );
    }

    public Map<String, BundleInfo> getInfoMap()
    {
        return this.infoMap;
    }

    public void bundle( final Object value, final int level )
    {
        final Dependency dep = this.project.getDependencies().add( CONFIG_NAME, value );
        final BundleInfo info = new BundleInfo( dep, level );
        this.infoMap.put( info.getId(), info );
    }

    public BundleInfo getInfo( final ResolvedArtifact artifact )
    {
        final String id = BundleInfo.toId( artifact );
        return this.infoMap.get( id );
    }

    public static BundlesExtension get( final Project project )
    {
        return project.getExtensions().getByType( BundlesExtension.class );
    }

    public static BundlesExtension create( final Project project )
    {
        return project.getExtensions().create( "bundles", BundlesExtension.class, project );
    }
}
