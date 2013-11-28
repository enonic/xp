package com.enonic.wem.portal.dispatch;


public class PortalRequest
{
    protected PortalRequestPath portalRequestPath;

    protected String workspace;

    protected String mode;

    public void createPortalRequestPath( )
    {
        this.portalRequestPath = new PortalRequestPath( );
    }

    public void appendPath( final String element )
    {
        portalRequestPath.appendPathElement( element );
    }

    public String getWorkspace()
    {
        return workspace;
    }

    public void setWorkspace( final String workspace )
    {
        this.workspace = workspace;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode( final String mode )
    {
        this.mode = mode;
    }

    public PortalRequestPath getPortalRequestPath()
    {
        return portalRequestPath;
    }


}


