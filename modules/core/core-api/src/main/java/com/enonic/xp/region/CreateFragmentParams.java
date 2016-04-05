package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;

@Beta
public final class CreateFragmentParams
{
    private final ContentPath parentPath;

    private final Component component;

    private final PropertyTree config;

    public CreateFragmentParams( final Builder builder )
    {
        this.parentPath = builder.parentPath;
        this.component = builder.component;
        this.config = builder.config == null ? new PropertyTree() : builder.config;
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

        private Builder()
        {
        }

        private Builder( final CreateFragmentParams source )
        {
            this.component = source.component;
            this.config = source.config;
            this.parentPath = source.parentPath;
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

        private void validate()
        {
            Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
            Preconditions.checkNotNull( component, "component cannot be null" );
        }

        public CreateFragmentParams build()
        {
            this.validate();
            return new CreateFragmentParams( this );
        }
    }
}
