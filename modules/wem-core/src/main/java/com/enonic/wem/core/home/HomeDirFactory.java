package com.enonic.wem.core.home;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.lifecycle.ProviderFactory;

@Component
@Profile("default")
public final class HomeDirFactory
    extends ProviderFactory<HomeDir>
{
    public HomeDirFactory()
    {
        super(HomeDir.class);
    }

    @Override
    public HomeDir get()
    {
        return HomeDir.get();
    }
}
