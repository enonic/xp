package com.enonic.xp.core.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ContentServiceImplTest_importContent
    extends AbstractContentServiceTest
{

    static final ProjectName destProjectName = ProjectName.from( "dest" );
    static Context ctxDraftDest()
    {
        return ContextBuilder.create().
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( destProjectName.getRepoId() ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();
    }

    @BeforeEach
    void initDestinationRepository()
    {
        projectService.create( CreateProjectParams.create().name( destProjectName ).displayName( "Destination project" ).build() );
    }

    @AfterEach
    void deleteDestinationRepository()
    {
        projectService.delete( destProjectName );
    }

    @Test
    void mergePermissionsWithParentOnCreate()
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Content sourceContent = createContent( ContentPath.ROOT, "content1", aclList );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( true ).
            build();

        final Content importedContent = ctxDraftDest().callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

        assertEquals( 8, importedContent.getPermissions().getAllPrincipals().getSize() );
        assertFalse( importedContent.getPermissions().getEntry( TEST_DEFAULT_USER.getKey() ).isAllowed( Permission.DELETE ) );
    }

    @Test
    void skipSourcePermissionsOnCreate()
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Content sourceContent = createContent( ContentPath.ROOT, "content1", aclList );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( false ).
            build();

        final Content importedContent = ctxDraftDest().callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

        assertNotEquals( sourceContent.getPermissions(), importedContent.getPermissions() );
    }
}
