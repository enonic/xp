package com.enonic.wem.servlet.jaxrs;

import java.util.Set;

public interface JaxRsContributor
    extends JaxRsComponent
{
    public Set<Object> getSingletons();
}
