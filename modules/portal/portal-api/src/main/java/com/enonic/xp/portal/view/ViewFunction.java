package com.enonic.xp.portal.view;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ViewFunction
{
    String getName();

    Object execute( ViewFunctionParams params );
}
