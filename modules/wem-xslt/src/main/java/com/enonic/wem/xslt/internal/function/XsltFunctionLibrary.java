package com.enonic.wem.xslt.internal.function;

import net.sf.saxon.Configuration;

import com.enonic.wem.portal.view.ViewFunctions;

public final class XsltFunctionLibrary
{
    private final ViewFunctions viewFunctions;

    public XsltFunctionLibrary( final ViewFunctions viewFunctions )
    {
        this.viewFunctions = viewFunctions;
    }

    public void registerAll( final Configuration config )
    {
        config.registerExtensionFunction( new GenericUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new PageUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new ImageUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new AssetUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new AttachmentUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new ServiceUrlFunction( this.viewFunctions ) );
        config.registerExtensionFunction( new ComponentUrlFunction( this.viewFunctions ) );
    }
}
