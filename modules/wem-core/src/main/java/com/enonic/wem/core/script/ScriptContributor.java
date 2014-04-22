package com.enonic.wem.core.script;

import java.net.URL;
import java.util.List;

import javax.script.Bindings;

public interface ScriptContributor
{
    public int getOrder();

    public List<URL> getScripts();

    public void applyBindings( Bindings bindings );
}
