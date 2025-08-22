package com.enonic.xp.lib.project.mapper;

import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.SiteConfig;

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
        gen.value( "language", language != null ? language.toLanguageTag() : null );

        serializeParents( gen );
        serializeSiteConfigs( gen );
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

    private void serializeSiteConfigs( final MapGenerator gen )
    {
        if ( !project.getSiteConfigs().isEmpty() )
        {
            gen.array( "siteConfig" );
            for ( final SiteConfig siteConfig : project.getSiteConfigs() )
            {
                gen.map();
                gen.value( "applicationKey", siteConfig.getApplicationKey() );

                gen.map( "config" );
                new PropertyTreeMapper( siteConfig.getConfig() ).serialize( gen );
                gen.end();

                gen.end();
            }
            gen.end();
        }
    }

    private void serializeParents( final MapGenerator gen )
    {
        gen.array( "parents" );
        project.getParents().forEach( gen::value );
        gen.end();

        gen.value( "parent", project.getParent() );
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
            Objects.requireNonNull( project, "project is required" );
        }


        public ProjectMapper build()
        {
            validate();
            return new ProjectMapper( this );
        }
    }
}

