package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

public class FindPrincipalsResultJson
{

    private final List<PrincipalJson> principalsJson;

    private final Integer totalSize;

    public FindPrincipalsResultJson( final List<PrincipalJson> principalsJson, final Integer totalSize )
    {
        this.principalsJson = principalsJson;
        this.totalSize = totalSize;
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