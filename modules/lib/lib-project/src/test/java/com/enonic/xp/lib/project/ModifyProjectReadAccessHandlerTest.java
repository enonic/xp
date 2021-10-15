package com.enonic.xp.lib.project;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.acl.AccessControlList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ModifyProjectReadAccessHandlerTest
    extends BaseProjectHandlerTest
{
    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        when( this.contentService.applyPermissions( any( ApplyContentPermissionsParams.class ) ) ).thenAnswer( mock -> {
            final ApplyContentPermissionsParams params = (ApplyContentPermissionsParams) mock.getArguments()[0];

            Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( Content.create().id( ContentId.from( "123" ) ).
                name( ContentName.from( "root" ) ).
                parentPath( ContentPath.ROOT ).
                permissions( AccessControlList.empty() ).
                data( new PropertyTree() ).
                extraDatas( ExtraDatas.empty() ).permissions( params.getPermissions() ).build() );

            return null;
        } );
    }

    @Test
    public void modifyReadAccess()
        throws Exception
    {
        runFunction( "/test/ModifyProjectReadAccessHandlerTest.js", "modifyReadAccess" );
    }

    @Test
    public void modifyReadAccessNull()
        throws Exception
    {
        runFunction( "/test/ModifyProjectReadAccessHandlerTest.js", "modifyReadAccessNull" );
    }


}
