package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.idprovider.IdProviderDescriptorMode;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class IdProviderJson
    extends IdProviderSummaryJson
{
    private final IdProviderDescriptorMode idProviderMode;

    private final List<IdProviderAccessControlEntryJson> permissions;

    public IdProviderJson( final IdProvider idProvider, final IdProviderDescriptorMode idProviderMode,
                           final IdProviderAccessControlList idProviderAccessControlList, final Principals principals )
    {
        super( idProvider );
        this.idProviderMode = idProviderMode;
        this.permissions = new ArrayList<>();
        if ( idProviderAccessControlList != null )
        {
            for ( IdProviderAccessControlEntry entry : idProviderAccessControlList )
            {
                final Principal principal = principals.getPrincipal( entry.getPrincipal() );
                if ( principal != null )
                {
                    this.permissions.add( new IdProviderAccessControlEntryJson( entry, principal ) );
                }
            }
        }
    }

    public String getIdProviderMode()
    {
        return idProviderMode == null ? null : idProviderMode.toString();
    }

    public List<IdProviderAccessControlEntryJson> getPermissions()
    {
        return this.permissions;
    }
}
