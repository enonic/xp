package com.enonic.xp.lib.mail;

import java.util.function.Supplier;

import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GetDefaultFromEmailHandler
    implements ScriptBean
{

    private Supplier<MailService> mailService;

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }

    public String execute()
    {
        return this.mailService.get().getDefaultFromEmail();
    }
}
