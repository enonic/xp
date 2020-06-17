package com.enonic.xp.project;

import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.Repository;

@PublicApi
public final class Project
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final ProjectName parent;

    private final Attachment icon;

    private Project( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.parent = builder.parent;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Project from( final Repository repository )
    {
        if ( repository == null )
        {
            return null;
        }

        final PropertyTree repositoryData = repository.getData();

        //TODO: remove default project data for XP8
        if ( repositoryData == null )
        {
            return ContentConstants.CONTENT_REPO_ID.equals( repository.getId() ) ? ProjectConstants.DEFAULT_PROJECT : null;
        }

        final PropertySet projectData = repositoryData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( projectData == null )
        {
            return ContentConstants.CONTENT_REPO_ID.equals( repository.getId() ) ? ProjectConstants.DEFAULT_PROJECT : null;
        }

        final Project.Builder project = Project.create().
            name( ProjectName.from( repository.getId() ) ).
            description( projectData.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).
            displayName( projectData.getString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY ) );

        buildParents( project, projectData );
        buildIcon( project, projectData );

        return project.build();
    }

    private static void buildIcon( final Project.Builder project, final PropertySet projectData )
    {
        final PropertySet iconData = projectData.getPropertySet( ProjectConstants.PROJECT_ICON_PROPERTY );

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
    }

    private static void buildParents( final Project.Builder project, final PropertySet projectData )
    {
        final Iterator<String> projectNamesIterator = projectData.getStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY ).iterator();
        if ( projectNamesIterator.hasNext() )
        {
            project.parent( ProjectName.from( projectNamesIterator.next() ) );
        }
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

    public ProjectName getParent()
    {
        return parent;
    }

    public static final class Builder
    {

        private ProjectName name;

        private String displayName;

        private String description;

        private ProjectName parent;

        private Attachment icon;

        private Builder()
        {
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

        public Builder parent( final ProjectName parent )
        {
            this.parent = parent;
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
