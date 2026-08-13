package com.enonic.xp.lib.admin;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.admin.event.RegisterTopicParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class CreateTopicHandler
    implements ScriptBean
{
    private Supplier<AdminEventHub> adminEventHub;

    private ApplicationKey applicationKey;

    private String name;

    private List<String> allow = List.of();

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setAllow( final ScriptValue allow )
    {
        this.allow = allow != null ? allow.getArray( String.class ) : List.of();
    }

    public String execute()
    {
        return this.adminEventHub.get()
            .registerTopic( RegisterTopicParams.create()
                                .owner( this.applicationKey )
                                .name( this.name )
                                .allow( PrincipalKeys.from( this.allow.stream().map( PrincipalKey::from ).toList() ) )
                                .build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        // the owner half of the canonical topic name comes from the bean context, never from
        // script input: an application cannot register, or later address, another's topic
        this.applicationKey = context.getApplicationKey();
        this.adminEventHub = context.getService( AdminEventHub.class );
    }
}
