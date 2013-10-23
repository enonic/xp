package com.enonic.wem.runner;

import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

final class NopJarScanner
    implements JarScanner
{
    @Override
    public void scan( final ServletContext servletContext, final ClassLoader classLoader, final JarScannerCallback jarScannerCallback,
                      final Set<String> strings )
    {
        // Do nothing
    }
}
