package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;

public class ContentConstants
{
    // TODO: THIS IS JUST TEMPORARY, THE CONTEXT SHOULD BE CREATED WHERE NEEDED
    private static final Workspace DEFAULT_WORKSPACE = new Workspace( "stage" );

    public static final Context DEFAULT_CONTEXT = new Context( DEFAULT_WORKSPACE );
}
