package com.enonic.xp.web.jmx.impl;

import javax.management.ObjectName;

import org.jolokia.restrictor.Restrictor;
import org.jolokia.util.HttpMethod;
import org.jolokia.util.RequestType;

final class RestrictorImpl
    implements Restrictor
{
    @Override
    public boolean isHttpMethodAllowed( final HttpMethod method )
    {
        return true;
    }

    @Override
    public boolean isTypeAllowed( final RequestType type )
    {
        return true;
    }

    @Override
    public boolean isAttributeReadAllowed( final ObjectName name, final String attribute )
    {
        return true;
    }

    @Override
    public boolean isAttributeWriteAllowed( final ObjectName name, final String attribute )
    {
        return false;
    }

    @Override
    public boolean isOperationAllowed( final ObjectName name, final String operation )
    {
        return false;
    }

    @Override
    public boolean isRemoteAccessAllowed( final String... hostOrAddress )
    {
        return true;
    }

    @Override
    public boolean isOriginAllowed( final String origin, final boolean isStrictCheck )
    {
        return true;
    }
}
