package com.enonic.xp.core.impl.export.validator;

import com.enonic.wem.api.node.CreateNodeParams;

public interface ImportValidator
{
    public CreateNodeParams ensureValid( final CreateNodeParams original );


    public boolean canHandle( final CreateNodeParams original );

}
