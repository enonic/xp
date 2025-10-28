package com.enonic.xp.repo.impl.elasticsearch;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItem;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessControlListIndexDocumentFactoryTest
{
    @Test
    void single_user()
    {

        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                build() ).
            build();

        final List<IndexItem> aclStoreDocumentItems = AccessControlListStoreDocumentFactory.create( acl );

        assertEquals( 1, aclStoreDocumentItems.size() );
    }

    @Test
    void multiple_user()
    {
        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( PrincipalKey.from( "user:myidprovider:tsi" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                allow( Permission.DELETE ).
                principal( PrincipalKey.from( "user:myidprovider:aro" ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.DELETE ).
                principal( PrincipalKey.from( "user:myidprovider:srs" ) ).
                build() ).
            build();

        final List<IndexItem> aclStoreDocumentItems = AccessControlListStoreDocumentFactory.create( acl );

        assertEquals( 5, aclStoreDocumentItems.size() );
    }

    @Test
    void single_user_all_permissions()
    {
        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                allow( Permission.PUBLISH ).
                allow( Permission.WRITE_PERMISSIONS ).
                allow( Permission.READ_PERMISSIONS ).
                allow( Permission.MODIFY ).
                allow( Permission.CREATE ).
                allow( Permission.DELETE ).
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                build() ).
            build();

        final List<IndexItem> aclStoreDocumentItems = AccessControlListStoreDocumentFactory.create( acl );

        assertEquals( 7, aclStoreDocumentItems.size() );
    }

}
