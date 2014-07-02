package com.enonic.wem.jaxrs.internal;

import com.enonic.wem.jaxrs.JaxRsContributor;

public interface JaxRsListener
{
    public void add( JaxRsContributor instance );

    public void remove( JaxRsContributor instance );
}
