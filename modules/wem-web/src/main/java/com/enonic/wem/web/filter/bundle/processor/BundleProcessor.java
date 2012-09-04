package com.enonic.wem.web.filter.bundle.processor;

import com.enonic.wem.web.filter.bundle.BundleRequest;

public interface BundleProcessor
{
    public String process( BundleRequest req )
        throws Exception;
}
