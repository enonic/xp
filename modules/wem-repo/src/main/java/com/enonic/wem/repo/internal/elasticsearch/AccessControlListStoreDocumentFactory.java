package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentStringItem;

public class AccessControlListStoreDocumentFactory
{
    static Set<AbstractStoreDocumentItem> create( final AccessControlList accessControlList )
    {
        final Set<AbstractStoreDocumentItem> aclStoreDocumentItems = Sets.newHashSet();

        final PrincipalKeys hasRead = accessControlList.getPrincipalsWithPermission( Permission.READ );

        for ( final PrincipalKey principalKey : hasRead )
        {
            aclStoreDocumentItems.add( new StoreDocumentStringItem( IndexPaths.HAS_READ_PATH, principalKey.toString() ) );
        }

        return aclStoreDocumentItems;
    }

}
