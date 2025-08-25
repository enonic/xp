package com.enonic.xp.region;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public final class CreateFragmentParams
{
    private final ContentPath parentPath;

    private final Component component;

    private final PropertyTree config;

    private final WorkflowInfo workflowInfo;

    private CreateFragmentParams( final Builder builder )
    {
        this.parentPath = builder.parentPath;
        this.component = builder.component;
        this.config = builder.config == null ? new PropertyTree() : builder.config;
        this.workflowInfo = builder.workflowInfo;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    public ContentPath getParent()
    {
        return parentPath;
    }

    public Component getComponent()
    {
        return component;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final CreateFragmentParams that = (CreateFragmentParams) o;
        return Objects.equals( parentPath, that.parentPath ) &&
            Objects.equals( component, that.component ) &&
            Objects.equals( config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parentPath, component, config );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateFragmentParams source )
    {
        return new Builder( source );
    }

    public static final class Builder
    {
        private ContentPath parentPath;

        private Component component;

        private PropertyTree config;

        private WorkflowInfo workflowInfo;

        private Builder()
        {
        }

        private Builder( final CreateFragmentParams source )
        {
            this.component = source.component;
            this.config = source.config;
            this.parentPath = source.parentPath;
            this.workflowInfo = source.workflowInfo;
        }

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public Builder parent( final ContentPath parentContentPath )
        {
            this.parentPath = parentContentPath;
            return this;
        }

        public Builder component( final Component component )
        {
            this.component = component;
            return this;
        }

        public Builder workflowInfo( final WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( parentPath, "parentPath is required" );
            Objects.requireNonNull( component, "component is required" );
            Objects.requireNonNull( workflowInfo, "workflowInfo is required" );
        }

        public CreateFragmentParams build()
        {
            this.validate();
            return new CreateFragmentParams( this );
        }
    }
}
