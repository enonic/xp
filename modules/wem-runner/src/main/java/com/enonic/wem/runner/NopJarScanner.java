package com.enonic.wem.runner;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

final class NopJarScanner
    implements JarScanner
{
    @Override
    public void scan( final JarScanType scanType, final ServletContext context, final JarScannerCallback callback )
    {
        // Do nothing
    }

    @Override
    public JarScanFilter getJarScanFilter()
    {
        return null;
    }

    @Override
    public void setJarScanFilter( final JarScanFilter jarScanFilter )
    {
        // Do nothing
    }
}
