package com.enonic.xp.lib.project;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.EditableProject;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectNotFoundException;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.project.SetProjectReadAccessParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseProjectHandlerTest
    extends ScriptTestSupport
{
    protected ContentService contentService;

    protected ProjectService projectService;

    protected PropertyTreeMarshallerService propertyTreeMarshallerService;

    protected void mockRootContent()
    {
        final Content.Builder contentRoot = Content.create()
            .id( ContentId.from( "123" ) )
            .name( ContentName.from( "root" ) )
            .language( Locale.JAPANESE )
            .parentPath( ContentPath.ROOT )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
                              .build() )
            .data( new PropertyTree() )
            .mixins( Mixins.empty() );

        when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot.build() );
        when( contentService.patch( any( PatchContentParams.class ) ) ).thenAnswer( invocation -> {
            final PatchContentParams params = invocation.getArgument( 0 );
            final PatchableContent patchable = new PatchableContent( contentRoot.build() );
            params.getPatcher().patch( patchable );
            final Content patched = patchable.build();

            contentRoot.language( patched.getLanguage() );
            when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot.build() );
            return PatchContentResult.create()
                .contentId( params.getContentId() )
                .addResult( ContentConstants.BRANCH_DRAFT, patched )
                .addResult( ContentConstants.BRANCH_MASTER, patched )
                .build();
        } );
    }

    protected void mockProject()
    {
        final Map<ProjectName, Project> projects = new HashMap<>();

        when( this.projectService.create( any( CreateProjectParams.class ) ) ).thenAnswer( invocation -> {
            final CreateProjectParams params = invocation.getArgument( 0 );
            final Project project = createProject( params );
            when( projectService.get( project.getName() ) ).thenReturn( project );
            when( projectService.delete( project.getName() ) ).thenReturn( true );

            when( projectService.getReadAccess( project.getName() ) ).thenReturn( params.isPublic() );

            projects.put( project.getName(), project );

            return project;
        } );

        when( this.projectService.modify( any( ModifyProjectParams.class ) ) ).thenAnswer( invocation -> {
            final ModifyProjectParams params = invocation.getArgument( 0 );
            final Project existing = projects.get( params.getName() );
            if ( existing == null )
            {
                throw new ProjectNotFoundException( params.getName() );
            }
            final Project project = modifyProject( existing, params );
            when( projectService.get( project.getName() ) ).thenReturn( project );

            projects.put( project.getName(), project );

            return project;
        } );

        when( this.projectService.setReadAccess( any( SetProjectReadAccessParams.class ) ) ).thenAnswer( invocation -> {
            final SetProjectReadAccessParams setParams = invocation.getArgument( 0 );
            when( projectService.getReadAccess( setParams.getName() ) ).thenReturn( setParams.isPublic() );
            return setParams.isPublic();
        } );

        when( this.projectService.list() ).thenAnswer( mock -> Projects.create().addAll( projects.values() ).build() );

        when( this.projectService.getAvailableApplications( any( ProjectName.class ) ) ).thenAnswer( invocation -> {

            final Project project = projects.get( invocation.getArgument( 0, ProjectName.class ) );

            if ( project == null )
            {
                throw new ProjectNotFoundException( invocation.getArgument( 0 ) );
            }
            return ApplicationKeys.from(
                project.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( Collectors.toList() ) );
        } );

        when( this.projectService.getPermissions( any( ProjectName.class ) ) ).thenReturn( ProjectPermissions.create().build() );

        when( this.projectService.modifyPermissions( any( ProjectName.class ), any( ProjectPermissions.class ) ) ).thenAnswer(
            invocation -> {
                final ProjectName projectName = invocation.getArgument( 0 );
                final ProjectPermissions projectPermissions = invocation.getArgument( 1 );
                when( projectService.getPermissions( projectName ) ).thenReturn( projectPermissions );

                return projectPermissions;
            } );
    }

    private Project modifyProject( final Project existing, final ModifyProjectParams params )
    {
        final EditableProject editable = new EditableProject( existing );
        params.getEditor().edit( editable );

        final Project.Builder builder = Project.create();
        builder.name( existing.getName() );
        builder.displayName( editable.displayName );
        builder.description( editable.description );
        builder.language( editable.language );
        existing.getParents().forEach( builder::addParent );

        if ( editable.siteConfigs != null )
        {
            editable.siteConfigs.forEach( builder::addSiteConfig );
        }

        return builder.build();
    }

    private Project createProject( final CreateProjectParams params )
    {
        final Project.Builder builder = Project.create();
        builder.name( params.getName() );
        builder.displayName( params.getDisplayName() );
        builder.description( params.getDescription() );
        builder.language( params.getLanguage() );

        params.getParents().forEach( builder::addParent );

        params.getSiteConfigs().forEach( builder::addSiteConfig );

        return builder.build();
    }


    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.contentService = Mockito.mock( ContentService.class );
        this.projectService = Mockito.mock( ProjectService.class );
        this.propertyTreeMarshallerService = Mockito.mock( PropertyTreeMarshallerService.class );
        addService( ContentService.class, this.contentService );
        addService( ProjectService.class, this.projectService );
        addService( PropertyTreeMarshallerService.class, this.propertyTreeMarshallerService );

        mockProject();
        mockRootContent();
    }
}
