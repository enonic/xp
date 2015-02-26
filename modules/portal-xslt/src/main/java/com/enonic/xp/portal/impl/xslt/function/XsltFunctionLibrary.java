package com.enonic.xp.portal.impl.xslt.function;

import net.sf.saxon.Configuration;

import com.enonic.xp.portal.view.ViewFunctionService;

public final class XsltFunctionLibrary
{
    private final ViewFunctionService viewFunctionService;

    public XsltFunctionLibrary( final ViewFunctionService viewFunctionService )
    {
        this.viewFunctionService = viewFunctionService;
    }

    public void registerAll( final Configuration config )
    {
        register( config, "pageUrl" );
        register( config, "imageUrl" );
        register( config, "assetUrl" );
        register( config, "attachmentUrl" );
        register( config, "serviceUrl" );
        register( config, "componentUrl" );
        register( config, "imagePlaceholder" );
    }

    private void register( final Configuration config, final String name )
    {
        final XsltViewFunction function = new XsltViewFunction( name );
        function.service = this.viewFunctionService;
        config.registerExtensionFunction( function );
    }
}
