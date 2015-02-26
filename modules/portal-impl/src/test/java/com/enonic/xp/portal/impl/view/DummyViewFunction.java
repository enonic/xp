package com.enonic.xp.portal.impl.view;

import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

public final class DummyViewFunction
    implements ViewFunction
{
    @Override
    public String getName()
    {
        return "dummy";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        return "Hello Dummy";
    }
}
