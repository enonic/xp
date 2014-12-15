package com.enonic.wem.xslt.internal.function;

import net.sf.saxon.Configuration;

public final class XsltFunctionLibrary
{
    public void registerAll( final Configuration config )
    {
        config.registerExtensionFunction( new GeneralUrlFunction() );
        config.registerExtensionFunction( new PageUrlFunction() );
        config.registerExtensionFunction( new ImageUrlFunction() );
        config.registerExtensionFunction( new AssetUrlFunction() );
        config.registerExtensionFunction( new AttachmentUrlFunction() );
        config.registerExtensionFunction( new ServiceUrlFunction() );
        config.registerExtensionFunction( new ComponentUrlFunction() );
    }
}
