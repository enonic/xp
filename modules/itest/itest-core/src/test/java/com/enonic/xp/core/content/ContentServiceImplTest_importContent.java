package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContentServiceImplTest_importContent
    extends AbstractContentServiceTest
{

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

        final Content sourceContent = ctxDraft().callWith( () -> createContent( ContentPath.ROOT, "content1", aclList ) );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( true ).
            build();

        final Content importedContent = ctxMaster().callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

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

        final Content sourceContent = ctxDraft().callWith( () -> createContent( ContentPath.ROOT, "content1", aclList ) );

        final ImportContentParams importContentParams = ImportContentParams.create().
            importContent( sourceContent ).
            targetPath( ContentPath.from( ContentPath.ROOT, sourceContent.getName().toString() ) ).
            importPermissionsOnCreate( false ).
            build();

        final Content importedContent = ctxMaster().callWith( () -> this.contentService.importContent( importContentParams ).getContent() );

        assertNotEquals( sourceContent.getPermissions(), importedContent.getPermissions() );
    }
}
