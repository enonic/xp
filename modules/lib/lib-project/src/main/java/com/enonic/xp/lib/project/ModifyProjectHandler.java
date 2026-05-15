package com.enonic.xp.lib.project;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.EditableProject;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectEditor;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;

public final class ModifyProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private ScriptValue editor;

    @Override
    protected ProjectMapper doExecute()
    {
        final ModifyProjectParams params = ModifyProjectParams.create().name( this.id ).editor( newProjectEditor() ).build();

        final Project project = this.projectService.get().modify( params );

        final boolean isPublic = this.projectService.get().getReadAccess( this.id );

        final ProjectPermissions projectPermissions = this.projectService.get().getPermissions( this.id );

        return ProjectMapper.create()
            .setProject( project )
            .setLanguage( project.getLanguage() )
            .setProjectPermissions( projectPermissions )
            .setIsPublic( isPublic )
            .build();
    }

    private ProjectEditor newProjectEditor()
    {
        return edit -> {
            if ( this.editor == null )
            {
                return;
            }
            final ScriptValue value = this.editor.call( ProjectMapper.create().setProject( edit.source ).build() );
            if ( value != null )
            {
                applyEdit( edit, value.getMap() );
            }
        };
    }

    private void applyEdit( final EditableProject target, final Map<String, ?> map )
    {
        edit( map, "displayName", String.class, val -> target.displayName = val.orElse( null ) );
        edit( map, "description", String.class, val -> target.description = val.orElse( null ) );
        edit( map, "language", String.class, val -> target.language = val.map( Locale::forLanguageTag ).orElse( null ) );
        if ( map.containsKey( "siteConfig" ) )
        {
            final Object raw = map.get( "siteConfig" );
            target.siteConfigs = raw instanceof List ? buildSiteConfigsFromMap( (List<?>) raw ) : SiteConfigs.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private SiteConfigs buildSiteConfigsFromMap( final List<?> raw )
    {
        final SiteConfigs.Builder builder = SiteConfigs.create();
        for ( final Object entry : raw )
        {
            if ( !( entry instanceof Map ) )
            {
                continue;
            }
            final Map<String, ?> entryMap = (Map<String, ?>) entry;
            final String applicationKey = Converters.convert( entryMap.get( "applicationKey" ), String.class );
            if ( applicationKey == null )
            {
                continue;
            }
            final Object configRaw = entryMap.get( "config" );
            final PropertyTree config =
                configRaw instanceof Map ? PropertyTree.fromMap( (Map<String, Object>) configRaw ) : new PropertyTree();
            builder.add( SiteConfig.create().application( ApplicationKey.from( applicationKey ) ).config( config ).build() );
        }
        return builder.build();
    }

    private <T> void edit( final Map<String, ?> map, final String key, final Class<T> type, final Consumer<Optional<T>> fieldEditor )
    {
        if ( map.containsKey( key ) )
        {
            fieldEditor.accept(
                Optional.ofNullable( map.get( key ) ).map( v -> requireNonNull( Converters.convert( v, type ), "cannot convert" ) ) );
        }
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
