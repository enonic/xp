package com.enonic.xp.portal.view;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ViewFunctionService
{
    Object execute( ViewFunctionParams params );
}
