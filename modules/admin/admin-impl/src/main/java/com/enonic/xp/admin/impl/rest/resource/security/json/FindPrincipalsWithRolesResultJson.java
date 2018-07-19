package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;

public class FindPrincipalsWithRolesResultJson
{
    private final List<PrincipalJson> principalsJson;

    private final Boolean hasMore;

    private final Integer unfilteredSize;

    public FindPrincipalsWithRolesResultJson( final Principals principals, final Integer unfilteredSize, final Integer unfilteredTotal )
    {
        this.principalsJson = new ArrayList<>();
        if ( principals != null )
        {
            for ( Principal principal : principals )
            {
                principalsJson.add( new PrincipalJson( principal ) );

            }
        }
        this.unfilteredSize = unfilteredSize;
        this.hasMore = unfilteredSize < unfilteredTotal;
    }

    public Integer getUnfilteredSize()
    {
        return unfilteredSize;
    }

    public Boolean getHasMore()
    {
        return hasMore;
    }

    public List<PrincipalJson> getPrincipals()
    {
        return principalsJson;
    }
}
