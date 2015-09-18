package com.enonic.xp.web.jaxrs.impl;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.Sets;

final class JaxRsApplication
    extends Application
{
    protected final Set<Object> singletons;

    public JaxRsApplication()
    {
        this.singletons = Sets.newConcurrentHashSet();
    }

    @Override
    public Set<Object> getSingletons()
    {
        return this.singletons;
    }
}
