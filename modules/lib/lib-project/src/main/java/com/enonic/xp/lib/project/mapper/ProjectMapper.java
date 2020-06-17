package com.enonic.xp.lib.project.mapper;

import java.util.Locale;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ProjectMapper
    implements MapSerializable
{
    private final Project project;

    private final Locale language;

    private final ProjectPermissions permissions;

    private final Boolean isPublic;

    public ProjectMapper( final Builder builder )
    {
        this.project = builder.project;
        this.permissions = builder.permissions;
        this.language = builder.language;
        this.isPublic = builder.isPublic;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        doSerialize( gen );
    }

    private void doSerialize( final MapGenerator gen )
    {
        gen.value( "id", project.getName().toString() );
        gen.value( "displayName", project.getDisplayName() );
        gen.value( "description", project.getDescription() );
        gen.value( "parent", project.getParent() );
        gen.value( "language", language != null ? language.toLanguageTag() : null );

        serializePermissions( gen );
        serializeReadAccess( gen );
    }

    private void serializePermissions( final MapGenerator gen )
    {
        new ProjectPermissionsMapper( permissions ).serialize( gen );
    }

    private void serializeReadAccess( final MapGenerator gen )
    {
        new ProjectReadAccessMapper( isPublic ).serialize( gen );
    }

    public static final class Builder
    {

        private Project project;

        private ProjectPermissions permissions;

        private Locale language;

        private Boolean isPublic;

        private Builder()
        {
        }

        public Builder setProject( final Project project )
        {
            this.project = project;
            return this;
        }

        public Builder setProjectPermissions( final ProjectPermissions permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder setLanguage( final Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder setIsPublic( final Boolean isPublic )
        {
            this.isPublic = isPublic;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( project, "project cannot be null" );
        }


        public ProjectMapper build()
        {
            validate();
            return new ProjectMapper( this );
        }
    }
}

