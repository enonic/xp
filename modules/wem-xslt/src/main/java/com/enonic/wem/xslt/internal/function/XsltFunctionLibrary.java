package com.enonic.wem.xslt.internal.function;

import net.sf.saxon.Configuration;

import com.enonic.wem.portal.view.ViewFunctions;

public final class XsltFunctionLibrary
{
    private final ViewFunctions functions;

    public XsltFunctionLibrary( final ViewFunctions functions )
    {
        this.functions = functions;
    }

    public void registerAll( final Configuration config )
    {
        config.registerExtensionFunction( new GeneralUrlFunction( this.functions ) );
        config.registerExtensionFunction( new PageUrlFunction( this.functions ) );
        config.registerExtensionFunction( new ImageUrlFunction( this.functions ) );
        config.registerExtensionFunction( new AssetUrlFunction( this.functions ) );
        config.registerExtensionFunction( new AttachmentUrlFunction( this.functions ) );
        config.registerExtensionFunction( new ServiceUrlFunction( this.functions ) );
        config.registerExtensionFunction( new ComponentUrlFunction( this.functions ) );
    }
}
