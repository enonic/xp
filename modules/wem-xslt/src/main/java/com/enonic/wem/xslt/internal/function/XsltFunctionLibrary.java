package com.enonic.wem.xslt.internal.function;

import net.sf.saxon.Configuration;

public final class XsltFunctionLibrary
{
    public void registerAll( final Configuration config )
    {
        config.registerExtensionFunction( new CreateUrlFunction() );
    }
}
