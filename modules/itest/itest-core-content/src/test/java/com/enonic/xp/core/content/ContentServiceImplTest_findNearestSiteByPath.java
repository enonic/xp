package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentServiceImplTest_findNearestSiteByPath
    extends AbstractContentServiceTest
{
    @Test
    void find_exact_path()
    {
        final Content site = createContent( ContentPath.ROOT, "a" , new PropertyTree(), ContentTypeName.site() );
        final Content site2 = createContent( site.getPath(), "b" , new PropertyTree(), ContentTypeName.site() );

        final Content child1 = createContent( site2.getPath(), "c" );
        createContent( child1.getPath(), "d" );

        final Content closestByPath = contentService.findNearestSiteByPath( ContentPath.from( "/a/b" ) );
        assertEquals( site2.getPath(), closestByPath.getPath() );
    }

    @Test
    void find_site_on_existing_content()
    {
        final Content site = createContent( ContentPath.ROOT, "a" , new PropertyTree(), ContentTypeName.site() );
        final Content site2 = createContent( site.getPath(), "b" , new PropertyTree(), ContentTypeName.site() );

        final Content child1 = createContent( site2.getPath(), "c" );
        createContent( child1.getPath(), "d" );

        final Content closestByPath = contentService.findNearestSiteByPath( ContentPath.from( "/a/b/c/d" ) );
        assertEquals( site2.getPath(), closestByPath.getPath() );
    }

    @Test
    void find_site_on_non_existing_content()
    {
        final Content site = createContent( ContentPath.ROOT, "a" , new PropertyTree(), ContentTypeName.site() );
        final Content site2 = createContent( site.getPath(), "b" , new PropertyTree(), ContentTypeName.site() );

        createContent( site2.getPath(), "c" );

        final Content closestByPath = contentService.findNearestSiteByPath( ContentPath.from( "/a/b/c/d" ) );
        assertEquals( site2.getPath(), closestByPath.getPath() );
    }

    @Test
    void find_When_Some_Parent_In_Path_Doest_Not_Have_Read_Permission()
    {
        final Content site = createContent( ContentPath.ROOT, "a" , new PropertyTree(), ContentTypeName.site() );
        final Content site2 = createContent( site.getPath(), "b" , new PropertyTree(), ContentTypeName.site() );

        final Content child1 = createContent( site.getPath(), "c", AccessControlList.create().
            add( AccessControlEntry.create().
            principal( RoleKeys.AUTHENTICATED ).
            allowAll().
            build() ).
            add( AccessControlEntry.create().
            principal( RoleKeys.EVERYONE ).
            allowAll().
            deny( Permission.READ ).
            build() ).
            build() );

        createContent( child1.getPath(), "d" );

        final Content closestByPath = contentService.findNearestSiteByPath( ContentPath.from( "/a/b/c/d" ) );
        assertEquals( site2.getPath(), closestByPath.getPath() );
    }
}
