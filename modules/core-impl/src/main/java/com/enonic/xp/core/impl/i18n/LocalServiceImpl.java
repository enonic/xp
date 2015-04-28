package com.enonic.xp.core.impl.i18n;

import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Sets;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;

@Component
public class LocalServiceImpl
    implements LocaleService
{

    @Override
    public MessageBundle getBundle( final ModuleKey module, final Locale locale )
    {
        return new MessageBundle()
        {
            @Override
            public Set<String> getKeys()
            {
                return Sets.newHashSet( "key", "local" );
            }

            @Override
            public String localize( final String key, final Object... args )
            {
                return "this is localized!";
            }

        };
    }
}
