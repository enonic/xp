package com.enonic.xp.core.impl.export.validator;

import com.enonic.xp.node.CreateNodeParams;

public interface ImportValidator
{
    CreateNodeParams ensureValid( CreateNodeParams original );

    boolean canHandle( CreateNodeParams original );
}
