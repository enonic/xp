package com.enonic.wem.xslt.internal.function;

import com.enonic.wem.portal.view.ViewFunctions;

final class AttachmentUrlFunction
    extends AbstractUrlFunction
{
    public AttachmentUrlFunction( final ViewFunctions functions )
    {
        super( "attachmentUrl", functions );
    }

    @Override
    protected String execute( final String... params )
    {
        return this.functions.attachmentUrl( params );
    }
}
