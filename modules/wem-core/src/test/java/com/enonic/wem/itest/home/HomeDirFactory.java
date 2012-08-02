package com.enonic.wem.itest.home;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.cms.core.home.HomeDir;

@Component
@Profile("itest")
public final class HomeDirFactory
    implements FactoryBean<HomeDir>, InitializingBean, DisposableBean
{
    private HomeDir homeDir;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.homeDir = new HomeDir( Files.createTempDir() );
        copyFile( "config/cms.properties" );
        copyFile( "config/vhost.properties" );
    }

    @Override
    public void destroy()
    {
        FileUtils.deleteQuietly( this.homeDir.toFile() );
    }

    private void copyFile( final String resource )
        throws IOException
    {
        final String from = "/homeDir/" + resource;
        final File to = new File( this.homeDir.toFile(), resource );

        final URL url = getClass().getResource( from );
        if ( url == null )
        {
            throw new IOException( "Resource [" + from + "] not found" );
        }

        Files.createParentDirs( to );
        Files.copy( Resources.newInputStreamSupplier( url ), to );
    }

    public HomeDir getObject()
    {
        return this.homeDir;
    }

    public Class<?> getObjectType()
    {
        return HomeDir.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
