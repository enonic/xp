package com.enonic.xp.testing.mock;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

public final class MockViewFunctionService
    implements ViewFunctionService
{
    @Override
    public Object execute( final ViewFunctionParams params )
    {
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}
