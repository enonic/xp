package com.enonic.xp.web.vhost.impl.config;

final class VirtualHostMappingImpl
    implements VirtualHostMapping
{
    private final String name;

    private String host;

    private String source;

    private String target;

    public VirtualHostMappingImpl( final String name )
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getHost()
    {
        return this.host;
    }

    @Override
    public String getSource()
    {
        return this.source;
    }

    @Override
    public String getTarget()
    {
        return this.target;
    }

    public void setHost( final String host )
    {
        this.host = host;
    }

    public void setSource( final String source )
    {
        this.source = source;
    }

    public void setTarget( final String target )
    {
        this.target = target;
    }
}
