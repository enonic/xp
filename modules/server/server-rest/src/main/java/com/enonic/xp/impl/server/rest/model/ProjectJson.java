package com.enonic.xp.impl.server.rest.model;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.project.Project;

public class ProjectJson
{
    private final Project project;

    private final List<SiteJson> sites;

    private ProjectJson( Builder builder )
    {
        this.project = builder.project;
        this.sites = builder.sites.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return project.getName().toString();
    }

    public String getDisplayName()
    {
        return project.getDisplayName();
    }

    public String getDescription()
    {
        return project.getDescription();
    }

    public String getParentName()
    {
        return project.getParent() != null ? project.getParent().toString() : null;
    }

    public List<SiteJson> getSites()
    {
        return sites;
    }

    public static class Builder
    {
        private final ImmutableList.Builder<SiteJson> sites = ImmutableList.builder();

        private Project project;

        public Builder project( final Project project )
        {
            this.project = project;
            return this;
        }

        public Builder addSites( final Collection<SiteJson> siteJsons )
        {
            this.sites.addAll( siteJsons );
            return this;
        }

        public ProjectJson build()
        {
            return new ProjectJson( this );
        }
    }
}
