package com.enonic.wem.admin.jsp;

import java.io.OutputStream;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import com.enonic.wem.api.Client;

@Singleton
public final class LiveEditService
{
    private final Client client;

    @Inject
    public LiveEditService( final Client client )
    {
        this.client = client;
    }

    public Object getImage( String key )
    {
        return "Hello " + this.client + " -> " + key;
    }

    public void serveImage( String key, HttpServletResponse res )
        throws Exception
    {
        res.getOutputStream().write( getImage(key).toString().getBytes() );
    }
}
