package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.Principals;

public class PrincipalsJson
{

    private final List<PrincipalJson> principalsJson;

    public PrincipalsJson( final Principals principals )
    {
        this.principalsJson = new ArrayList<>();
        if ( principals != null )
        {
            for ( Principal principal : principals )
            {
                principalsJson.add( new PrincipalJson( principal ) );

            }
        }
    }

    public List<PrincipalJson> getPrincipals()
    {
        return principalsJson;
    }
}
