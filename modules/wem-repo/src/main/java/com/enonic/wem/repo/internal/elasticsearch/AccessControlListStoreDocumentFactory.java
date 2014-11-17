package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentStringItem;

public class AccessControlListStoreDocumentFactory
{
    static Set<AbstractStoreDocumentItem> create( final AccessControlList accessControlList )
    {
        final Set<AbstractStoreDocumentItem> aclStoreDocumentItems = Sets.newHashSet();

        final Set<PrincipalKey> hasRead = getPrincipalKeysWithRead( accessControlList );

        for ( final PrincipalKey principalKey : hasRead )
        {
            aclStoreDocumentItems.add( new StoreDocumentStringItem( IndexPaths.HAS_READ_PATH, principalKey.toString() ) );
        }

        return aclStoreDocumentItems;
    }

    static private Set<PrincipalKey> getPrincipalKeysWithRead( final AccessControlList accessControlList )
    {
        final Set<PrincipalKey> hasRead = Sets.newHashSet();

        for ( final AccessControlEntry entry : accessControlList )
        {
            if ( entry.isAllowed( Permission.READ ) )
            {
                hasRead.add( entry.getPrincipal() );
            }
        }
        return hasRead;
    }


}
