package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.acl.AccessControlList;

class ListProjectsHandlerTest
    extends BaseProjectHandlerTest
{
    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( Content.create().id( ContentId.from( "123" ) ).
            name( ContentName.from( "root" ) ).
            parentPath( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            data( new PropertyTree() ).
            mixins( Mixins.empty() ).build() );
    }

    @Test
    void listProjects()
    {
        runFunction( "/test/ListProjectsHandlerTest.js", "listProjects" );
    }

}
