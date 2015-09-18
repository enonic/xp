package com.enonic.xp.repo.impl.entity;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

/**
 * Default merging strategy:
 * <p>
 * Include all permissions that are in only one of the two ACLs, without modification.
 * For entries in both ACLs, merge according to the following rules:
 * - If permission is set in child entry, use the value from the child entry
 * - Otherwise if permission is set in parent entry, use the value from the parent entry
 * - Otherwise leave permission unset
 * <p>
 * Example:
 * Parent: user1[+create, -modify, -publish],          user2[+create, -modify]
 * Child : user1[+create, +modify, -delete],                                    user3[+create, -modify]
 * ------------------------------------------------------------------------------------------------------
 * Result: user1[+create, +modify, -delete, -publish], user2[+create, -modify], user3[+create, -modify]
 */
final class DefaultPermissionsMergingStrategy
    implements PermissionsMergingStrategy
{
    @Override
    public AccessControlList mergePermissions( final AccessControlList childAcl, final AccessControlList parentAcl )
    {
        final AccessControlList.Builder effective = AccessControlList.create();
        // apply parent entries
        for ( AccessControlEntry parentEntry : parentAcl )
        {
            final PrincipalKey principal = parentEntry.getPrincipal();
            if ( childAcl.contains( principal ) )
            {
                final AccessControlEntry childEntry = childAcl.getEntry( principal );
                final AccessControlEntry mergedEntry = mergeAccessControlEntries( childEntry, parentEntry );
                effective.add( mergedEntry );
            }
            else
            {
                effective.add( parentEntry );
            }
        }

        // apply child entries not in parent
        for ( AccessControlEntry childEntry : childAcl )
        {
            if ( !parentAcl.contains( childEntry.getPrincipal() ) )
            {
                effective.add( childEntry );
            }
        }

        return effective.build();
    }

    private AccessControlEntry mergeAccessControlEntries( final AccessControlEntry childEntry, final AccessControlEntry parentEntry )
    {
        final AccessControlEntry.Builder entry = AccessControlEntry.create().principal( childEntry.getPrincipal() );
        for ( Permission permission : Permission.values() )
        {
            if ( childEntry.isSet( permission ) )
            {
                // set effective permission from child
                if ( childEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
            else if ( parentEntry.isSet( permission ) )
            {
                // inherit permission from parent
                if ( parentEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
        }
        return entry.build();
    }

}
