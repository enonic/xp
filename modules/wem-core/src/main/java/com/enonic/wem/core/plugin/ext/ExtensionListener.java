package com.enonic.wem.core.plugin.ext;


import com.enonic.wem.api.plugin.ext.Extension;

public interface ExtensionListener
{
    public void extensionAdded( Extension ext );

    public void extensionRemoved( Extension ext );
}
