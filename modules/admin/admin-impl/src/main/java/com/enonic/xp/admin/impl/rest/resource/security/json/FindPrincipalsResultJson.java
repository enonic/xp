package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;

public class FindPrincipalsResultJson
{

    private final List<PrincipalJson> principalsJson;

    private final Integer totalSize;

    private final Integer hits;

    public FindPrincipalsResultJson( final Principals principals, final Integer hits, final Integer totalSize )
    {
        this.principalsJson = new ArrayList<>();
        if ( principals != null )
        {
            for ( Principal principal : principals )
            {
                principalsJson.add( new PrincipalJson( principal ) );

            }
        }
        this.totalSize = totalSize;
        this.hits = hits;
    }

    public Integer getHits()
    {
        return hits;
    }

    public Integer getTotalSize()
    {
        return totalSize;
    }

    public List<PrincipalJson> getPrincipals()
    {
        return principalsJson;
    }
}
