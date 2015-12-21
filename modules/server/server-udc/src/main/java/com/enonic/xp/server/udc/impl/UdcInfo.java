package com.enonic.xp.server.udc.impl;

final class UdcInfo
{
    private String product;

    private String version;

    private String versionHash;

    private String hardwareAddress;

    private String javaVersion;

    private String osName;

    private int count;

    private long maxMemory;

    private int numCpu;

    public String getProduct()
    {
        return product;
    }

    public void setProduct( final String product )
    {
        this.product = product;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public String getHardwareAddress()
    {
        return hardwareAddress;
    }

    public void setHardwareAddress( final String hardwareAddress )
    {
        this.hardwareAddress = hardwareAddress;
    }

    public String getJavaVersion()
    {
        return javaVersion;
    }

    public void setJavaVersion( final String javaVersion )
    {
        this.javaVersion = javaVersion;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount( final int count )
    {
        this.count = count;
    }

    public long getMaxMemory()
    {
        return maxMemory;
    }

    public void setMaxMemory( final long maxMemory )
    {
        this.maxMemory = maxMemory;
    }

    public int getNumCpu()
    {
        return numCpu;
    }

    public void setNumCpu( final int numCpu )
    {
        this.numCpu = numCpu;
    }

    public String getVersionHash()
    {
        return versionHash;
    }

    public void setVersionHash( final String versionHash )
    {
        this.versionHash = versionHash;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName( final String osName )
    {
        this.osName = osName;
    }
}
