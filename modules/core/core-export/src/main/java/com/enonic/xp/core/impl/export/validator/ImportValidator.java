package com.enonic.xp.core.impl.export.validator;

import com.enonic.xp.node.CreateNodeParams;

public interface ImportValidator
{
    CreateNodeParams ensureValid( final CreateNodeParams original );


    boolean canHandle( final CreateNodeParams original );

}
