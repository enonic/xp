package com.enonic.wem.core.jcr.accounts;

public class JcrUserStore
{
    private String id;

    private String name;

    private boolean defaultStore;

    private String connectorName;

    private String xmlConfig;

    public JcrUserStore()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public boolean isDefaultStore()
    {
        return defaultStore;
    }

    public void setDefaultStore( final boolean defaultStore )
    {
        this.defaultStore = defaultStore;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( final String connectorName )
    {
        this.connectorName = connectorName;
    }

    public String getXmlConfig()
    {
        return xmlConfig;
    }

    public void setXmlConfig( final String xmlConfig )
    {
        this.xmlConfig = xmlConfig;
    }
}
