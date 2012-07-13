package com.enonic.wem.itest.home;

import java.io.File;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.home.HomeDir;

@Component
@Profile("itest")
public final class HomeDirFactory
    implements FactoryBean<HomeDir>
{

    public HomeDir getObject()
    {
        return new HomeDir( new File( "./src/test/homeDir" ) );
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
