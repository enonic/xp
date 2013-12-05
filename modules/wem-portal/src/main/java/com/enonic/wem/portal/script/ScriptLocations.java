package com.enonic.wem.portal.script;

import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleName;

public final class ScriptLocations
{
    private final ClassLoader classLoader;

    private final String classLoaderPath;

    private final ImmutableMap<ModuleName, Path> modulePaths;

    private ScriptLocations( final Builder builder )
    {
        this.classLoader = builder.classLoader;
        this.classLoaderPath = builder.classLoaderPath;
        this.modulePaths = ImmutableMap.copyOf( builder.modulePaths );
    }

    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }

    public String getClassLoaderPath()
    {
        return this.classLoaderPath;
    }

    public Map<ModuleName, Path> getModulePaths()
    {
        return this.modulePaths;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private ClassLoader classLoader;

        private String classLoaderPath;

        private final Map<ModuleName, Path> modulePaths;

        private Builder()
        {
            this.classLoader = getClass().getClassLoader();
            this.classLoaderPath = "js/";
            this.modulePaths = Maps.newHashMap();
        }

        public Builder classLoader( final ClassLoader loader )
        {
            this.classLoader = loader;
            return this;
        }

        public Builder classLoaderPath( final String path )
        {
            this.classLoaderPath = path;
            return this;
        }

        public Builder modulePath( final ModuleName name, final Path path )
        {
            this.modulePaths.put( name, path );
            return this;
        }
    }
}
