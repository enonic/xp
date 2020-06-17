package com.enonic.xp.lib.project;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseProjectHandlerTest
    extends ScriptTestSupport
{
    protected ContentService contentService;

    protected ProjectService projectService;

    protected void mockRootContent()
    {
        final Content.Builder contentRoot = Content.create().id( ContentId.from( "123" ) ).
            name( ContentName.from( "root" ) ).
            parentPath( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            data( new PropertyTree() ).
            extraDatas( ExtraDatas.empty() );

        Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot.build() );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer( mock -> {

            final UpdateContentParams params = (UpdateContentParams) mock.getArguments()[0];
            final EditableContent editableContent = new EditableContent( contentRoot.build() );
            params.getEditor().edit( editableContent );

            contentRoot.language( editableContent.language );
            Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot.build() );
            return contentRoot.build();
        } );

        when( this.contentService.applyPermissions( any( ApplyContentPermissionsParams.class ) ) ).thenAnswer( mock -> {
            final ApplyContentPermissionsParams params = (ApplyContentPermissionsParams) mock.getArguments()[0];

            contentRoot.permissions( params.getPermissions() );
            Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot.build() );
            return null;
        } );
    }

    protected void mockProject()
    {
        final Map<ProjectName, Project> projects = new HashMap<>();

        when( this.projectService.create( any( CreateProjectParams.class ) ) ).thenAnswer( mock -> {

            final Project project = createProject( (CreateProjectParams) mock.getArguments()[0] );
            Mockito.when( projectService.get( project.getName() ) ).thenReturn( project );
            Mockito.when( projectService.delete( project.getName() ) ).thenReturn( true );

            projects.put( project.getName(), project );

            return project;
        } );

        when( this.projectService.modify( any( ModifyProjectParams.class ) ) ).thenAnswer( mock -> {

            final Project project = createProject( (ModifyProjectParams) mock.getArguments()[0] );
            Mockito.when( projectService.get( project.getName() ) ).thenReturn( project );

            projects.put( project.getName(), project );

            return project;
        } );

        when( this.projectService.list() ).thenAnswer( mock -> Projects.create().addAll( projects.values() ).build() );

        when( this.projectService.getPermissions( any( ProjectName.class ) ) ).thenReturn( ProjectPermissions.create().build() );

        when( this.projectService.modifyPermissions( any( ProjectName.class ), any( ProjectPermissions.class ) ) ).thenAnswer( mock -> {
            final ProjectName projectName = (ProjectName) mock.getArguments()[0];
            final ProjectPermissions projectPermissions = (ProjectPermissions) mock.getArguments()[1];
            Mockito.when( projectService.getPermissions( projectName ) ).thenReturn( projectPermissions );

            return projectPermissions;
        } );
    }

    private Project createProject( final ModifyProjectParams params )
    {
        final Project.Builder builder = Project.create();
        builder.name( params.getName() );
        builder.displayName( params.getDisplayName() );
        builder.description( params.getDescription() );

        return builder.build();
    }

    private Project createProject( final CreateProjectParams params )
    {
        final Project.Builder builder = Project.create();
        builder.name( params.getName() );
        builder.displayName( params.getDisplayName() );
        builder.description( params.getDescription() );
        builder.parent( params.getParent() );

        return builder.build();
    }


    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.contentService = Mockito.mock( ContentService.class );
        this.projectService = Mockito.mock( ProjectService.class );
        addService( ContentService.class, this.contentService );
        addService( ProjectService.class, this.projectService );

        mockProject();
        mockRootContent();

    }
}
