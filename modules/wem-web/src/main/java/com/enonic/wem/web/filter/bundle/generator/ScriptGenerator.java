package com.enonic.wem.web.filter.bundle.generator;

import com.enonic.wem.web.filter.bundle.BundleRequest;

public interface ScriptGenerator
{
    public String generate( BundleRequest req )
        throws Exception;
}
