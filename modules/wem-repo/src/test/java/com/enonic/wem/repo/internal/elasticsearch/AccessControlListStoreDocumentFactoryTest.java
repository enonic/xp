package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Set;

import org.junit.Test;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;

import static org.junit.Assert.*;

public class AccessControlListStoreDocumentFactoryTest
{


    @Test
    public void single_user()
        throws Exception
    {

        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myuserstore:rmy" ) ).
                build() ).
            build();

        final Set<AbstractStoreDocumentItem> aclStoreDocumentItems = AccessControlListStoreDocumentFactory.create( acl );

        assertEquals( 1, aclStoreDocumentItems.size() );
    }

    @Test
    public void multiple_user()
        throws Exception
    {

        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myuserstore:rmy" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myuserstore:tsi" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                allow( Permission.DELETE ).
                principal( PrincipalKey.from( "user:myuserstore:aro" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.DELETE ).
                principal( PrincipalKey.from( "user:myuserstore:srs" ) ).
                build() ).
            build();

        final Set<AbstractStoreDocumentItem> aclStoreDocumentItems = AccessControlListStoreDocumentFactory.create( acl );

        assertEquals( 3, aclStoreDocumentItems.size() );
    }


}