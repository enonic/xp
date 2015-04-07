package com.enonic.xp.portal.view;

import com.google.common.annotations.Beta;

@Beta
public interface ViewFunction
{
    String getName();

    Object execute( ViewFunctionParams params );
}
