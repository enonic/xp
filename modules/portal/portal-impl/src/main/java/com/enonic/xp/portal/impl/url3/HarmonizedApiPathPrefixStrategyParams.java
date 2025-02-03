package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.ProjectName;

public class HarmonizedApiPathPrefixStrategyParams
{
    private final PortalRequest portalRequest;

    private final ProjectName projectName;

    private final Branch branch;

    private final String contentKey;

    private HarmonizedApiPathPrefixStrategyParams( final Builder builder )
    {
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.contentKey = builder.contentKey;
    }

    public PortalRequest getPortalRequest()
    {
        return portalRequest;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public String getContentKey()
    {
        return contentKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PortalRequest portalRequest;

        private ProjectName projectName;

        private Branch branch;

        private String contentKey;

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder setProjectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setContentKey( final String contentKey )
        {
            this.contentKey = contentKey;
            return this;
        }

        public HarmonizedApiPathPrefixStrategyParams build()
        {
            if ( this.portalRequest == null )
            {
                Objects.requireNonNull( this.projectName );
                Objects.requireNonNull( this.branch );
                Objects.requireNonNull( this.contentKey );
            }
            else if ( projectName != null || branch != null || contentKey != null )
            {
                throw new IllegalArgumentException( "ProjectName, Branch and ContentKey must be null if PortalRequest is not null" );
            }

            return new HarmonizedApiPathPrefixStrategyParams( this );
        }
    }
}
