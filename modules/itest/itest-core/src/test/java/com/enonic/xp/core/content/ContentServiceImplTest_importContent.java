package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContentServiceImplTest_importContent
    extends AbstractContentServiceTest
{

    private static final Context CTX_DEFAULT = ContextBuilder.create().
        branch( WS_DEFAULT ).
        repositoryId( TEST_REPO_ID ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    private static final Context CTX_OTHER = ContextBuilder.create().
        branch( WS_OTHER ).
        repositoryId( TEST_REPO_ID ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    @Test
    public void keepSourcePermissionsOnCreate()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Content sourceContent = CTX_DEFAULT.callWith( () -> createContent( ContentPath.ROOT, "content1", aclList ) );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( true ).
            build();

        final Content importedContent = CTX_OTHER.callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

        assertEquals( sourceContent.getPermissions(), importedContent.getPermissions() );
    }

    @Test
    public void skipSourcePermissionsOnCreate()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Content sourceContent = CTX_DEFAULT.callWith( () -> createContent( ContentPath.ROOT, "content1", aclList ) );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( false ).
            build();

        final Content importedContent = CTX_OTHER.callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

        assertNotEquals( sourceContent.getPermissions(), importedContent.getPermissions() );
    }
}
