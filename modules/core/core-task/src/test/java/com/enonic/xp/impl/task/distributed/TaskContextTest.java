package com.enonic.xp.impl.task.distributed;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskContextTest
{

    @Test
    void serializable()
    {
        final TaskContext info = TaskContext.create()
            .setRepo( RepositoryId.from( "test" ) )
            .setBranch( Branch.from( "master" ) )
            .setAuthInfo( AuthenticationInfo.unAuthenticated() )
            .build();
        final byte[] serializedObject = SerializableUtils.serialize( info );
        final TaskContext deserializedObject = (TaskContext) SerializableUtils.deserialize( serializedObject );
        assertEquals( info.getRepo(), deserializedObject.getRepo() );
        assertEquals( info.getBranch(), deserializedObject.getBranch() );
        assertEquals( info.getContentRootPath(), deserializedObject.getContentRootPath() );
    }

}
