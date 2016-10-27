package com.enonic.xp.web.impl.status;

import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.status.StatusContext;

final class StatusContextImpl
    implements StatusContext
{
    private final HttpServletRequest request;

    private final OutputStream out;

    StatusContextImpl( final HttpServletRequest request, final OutputStream out )
    {
        this.request = request;
        this.out = out;
    }

    @Override
    public Optional<String> getParameter( final String name )
    {
        final String value = this.request.getParameter( name );
        return Optional.ofNullable( value );
    }

    @Override
    public OutputStream getOutputStream()
    {
        return this.out;
    }
}
