package com.enonic.wem.admin.app;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.JaxRsContributor;

public final class JaxRsResources
    implements JaxRsContributor
{
    private final Set<Object> set;

    public JaxRsResources()
    {
        this.set = Sets.newHashSet();
    }

    public void setResources( final List<Object> list )
    {
        this.set.addAll( list );
    }

    @Override
    public Set<Object> getSingletons()
    {
        return this.set;
    }
}
