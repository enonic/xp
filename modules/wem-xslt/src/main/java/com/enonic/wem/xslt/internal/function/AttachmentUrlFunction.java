package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.view.ViewFunctions;

final class AttachmentUrlFunction
    extends AbstractUrlFunction
{
    public AttachmentUrlFunction( final ViewFunctions functions )
    {
        super( "attachmentUrl", functions );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return this.functions.attachmentUrl( params );
    }
}
