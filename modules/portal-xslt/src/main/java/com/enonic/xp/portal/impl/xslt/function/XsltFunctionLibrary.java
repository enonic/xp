package com.enonic.xp.portal.impl.xslt.function;

import net.sf.saxon.Configuration;

import com.enonic.xp.portal.url.PortalUrlService;

public final class XsltFunctionLibrary
{
    private final PortalUrlService urlService;

    public XsltFunctionLibrary( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }

    public void registerAll( final Configuration config )
    {
        register( config, new PageUrlFunction() );
        register( config, new ImageUrlFunction() );
        register( config, new AssetUrlFunction() );
        register( config, new AttachmentUrlFunction() );
        register( config, new ServiceUrlFunction() );
        register( config, new ComponentUrlFunction() );
    }

    private void register( final Configuration config, final AbstractUrlFunction function )
    {
        function.urlService = urlService;
        config.registerExtensionFunction( function );
    }
}
