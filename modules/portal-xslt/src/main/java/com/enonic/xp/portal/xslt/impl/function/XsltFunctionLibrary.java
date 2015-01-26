package com.enonic.xp.portal.xslt.impl.function;

import net.sf.saxon.Configuration;

public final class XsltFunctionLibrary
{
    public void registerAll( final Configuration config )
    {
        config.registerExtensionFunction( new PageUrlFunction() );
        config.registerExtensionFunction( new ImageUrlFunction() );
        config.registerExtensionFunction( new AssetUrlFunction() );
        config.registerExtensionFunction( new AttachmentUrlFunction() );
        config.registerExtensionFunction( new ServiceUrlFunction() );
        config.registerExtensionFunction( new ComponentUrlFunction() );
    }
}
