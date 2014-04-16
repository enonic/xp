package com.enonic.wem.core.script.contributor;

import java.net.URL;
import java.util.List;

import javax.script.Bindings;

import com.google.common.collect.Lists;

import com.enonic.wem.core.script.ScriptContributor;

public final class SystemScriptContributor
    implements ScriptContributor
{
    private final static String LIB_PREFIX = "/system/js/";

    private final static String LIB_SUFFIX = ".js";

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public List<URL> getScripts()
    {
        final List<URL> list = Lists.newArrayList();
        list.add( getScriptUrl( "console" ) );
        return list;
    }

    private URL getScriptUrl( final String name )
    {
        return getClass().getResource( LIB_PREFIX + name + LIB_SUFFIX );
    }

    @Override
    public void applyBindings( final Bindings bindings )
    {
        // Do nothing
    }
}
