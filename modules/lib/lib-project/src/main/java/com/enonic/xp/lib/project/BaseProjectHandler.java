package com.enonic.xp.lib.project;

import java.util.Locale;
import java.util.Optional;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;

public abstract class BaseProjectHandler
    implements ScriptBean
{
    protected ContentService contentService;

    protected ProjectService projectService;

    public final Object execute()
    {
        validate();
        return this.doExecute();
    }

    protected abstract Object doExecute();

    protected void validate()
    {

    }

    protected Locale buildLanguage( final String value )
    {
        return Optional.ofNullable( value ).map( Locale::forLanguageTag ).orElse( null );
    }

    protected ProjectPermissions buildProjectPermissions( final ScriptValue value )
    {
        return Optional.ofNullable( value ).
            map( permissionsScriptValue -> {
                final ProjectPermissions.Builder builder = ProjectPermissions.create();

                Optional.ofNullable( permissionsScriptValue.getMember( "owner" ) ).ifPresent( scriptValue -> scriptValue.getList().forEach(
                    principalKey -> builder.addOwner( PrincipalKey.from( (String) principalKey ) ) ) );
                Optional.ofNullable( permissionsScriptValue.getMember( "editor" ) ).ifPresent( scriptValue -> scriptValue.getList().forEach(
                    principalKey -> builder.addEditor( PrincipalKey.from( (String) principalKey ) ) ) );
                Optional.ofNullable( permissionsScriptValue.getMember( "author" ) ).ifPresent( scriptValue -> scriptValue.getList().forEach(
                    principalKey -> builder.addAuthor( PrincipalKey.from( (String) principalKey ) ) ) );
                Optional.ofNullable( permissionsScriptValue.getMember( "contributor" ) ).ifPresent(
                    scriptValue -> scriptValue.getList().forEach(
                        principalKey -> builder.addContributor( PrincipalKey.from( (String) principalKey ) ) ) );
                Optional.ofNullable( permissionsScriptValue.getMember( "viewer" ) ).ifPresent( scriptValue -> scriptValue.getList().forEach(
                    principalKey -> builder.addViewer( PrincipalKey.from( (String) principalKey ) ) ) );

                return builder.build();
            } ).orElse( null );
    }

    protected boolean buildReadAccess( final ScriptValue value )
    {
        return Optional.ofNullable( value ).
            flatMap( readAccessValue -> Optional.ofNullable( readAccessValue.getMember( "public" ) ).
                map( ScriptValue::getValue ).
                map( isPublic -> ( (Boolean) isPublic ) ) ).
            orElseThrow( () -> new IllegalArgumentException( "Invalid value for readAccess." ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
        this.projectService = context.getService( ProjectService.class ).get();
    }
}
