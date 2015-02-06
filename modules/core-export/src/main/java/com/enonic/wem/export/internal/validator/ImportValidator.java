package com.enonic.wem.export.internal.validator;

import com.enonic.wem.api.node.CreateNodeParams;

public interface ImportValidator
{
    public CreateNodeParams ensureValid( final CreateNodeParams original );


    public boolean canHandle( final CreateNodeParams original );

}
