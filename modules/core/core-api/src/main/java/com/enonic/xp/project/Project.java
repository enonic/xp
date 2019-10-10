package com.enonic.xp.project;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.Repository;

@Beta
public final class Project
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final Attachment icon;

    private Project( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Project source )
    {
        return new Builder( source );
    }

    public static Project from( final Repository repository )
    {
        final PropertyTree repositoryData = repository.getData();

        if ( repositoryData == null )
        {
            return null;
        }

        final PropertySet projectData = repositoryData.getSet( "com-enonic-cms" );

        if ( projectData == null )
        {
            return null;
        }

        final Project.Builder project = Project.create().
            name( ProjectName.from( repository.getId() ) ).
            description( projectData.getString( "description" ) ).
            displayName( projectData.getString( "displayName" ) );

        final PropertySet iconData = projectData.getPropertySet( "icon" );

        if ( iconData != null )
        {
            project.icon( Attachment.create().
                name( iconData.getString( ContentPropertyNames.ATTACHMENT_NAME ) ).
                label( iconData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) ).
                mimeType( iconData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) ).
                size( iconData.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) ).
                textContent( iconData.getString( ContentPropertyNames.ATTACHMENT_TEXT ) ).
                build() );
        }

        return project.build();
    }

    public ProjectName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Attachment getIcon()
    {
        return icon;
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
        final Project project = (Project) o;
        return Objects.equals( name, project.name ) && Objects.equals( displayName, project.displayName ) &&
            Objects.equals( description, project.description ) && Objects.equals( icon, project.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, description, icon );
    }

    public static final class Builder
    {

        private ProjectName name;

        private String displayName;

        private String description;

        private Attachment icon;

        private Builder()
        {
        }

        public Builder( final Project source )
        {
            name = source.name;
            displayName = source.displayName;
            description = source.description;
            icon = source.icon;
        }

        public Builder name( final ProjectName value )
        {
            this.name = value;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder icon( final Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name cannot be null" );
        }


        public Project build()
        {
            validate();
            return new Project( this );
        }
    }
}
