package com.enonic.wem.core.script.contributor;

import java.net.URL;
import java.util.List;

import javax.script.Bindings;

import com.google.common.collect.Lists;

import com.enonic.wem.core.script.ScriptContributor;

public final class PortalScriptContributor
    implements ScriptContributor
{
    @Override
    public int getOrder()
    {
        return 10;
    }

    @Override
    public List<URL> getScripts()
    {
        return Lists.newArrayList();
    }

    @Override
    public void applyBindings( final Bindings bindings )
    {

    }
}
