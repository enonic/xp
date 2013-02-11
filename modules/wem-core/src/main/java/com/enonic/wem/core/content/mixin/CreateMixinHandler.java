package com.enonic.wem.core.content.mixin;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.mixin.CreateMixin;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.mixin.dao.MixinDao;
import com.enonic.wem.core.time.TimeService;

import static com.enonic.wem.api.content.mixin.Mixin.newMixin;

@Component
public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    private TimeService timeService;


    public CreateMixinHandler()
    {
        super( CreateMixin.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateMixin command )
        throws Exception
    {
        final DateTime currentTime = timeService.getNowAsDateTime();
        final Mixin.Builder mixinBuilder = newMixin();
        mixinBuilder.formItem( command.getFormItem() );
        mixinBuilder.displayName( command.getDisplayName() );
        mixinBuilder.module( command.getModuleName() );
        mixinBuilder.createdTime( currentTime );
        mixinBuilder.modifiedTime( currentTime );
        mixinBuilder.icon( command.getIcon() );

        final Mixin mixin = mixinBuilder.build();

        final Session session = context.getJcrSession();
        mixinDao.create( mixin, session );
        session.save();

        command.setResult( mixin.getQualifiedName() );
    }

    @Autowired
    public void setMixinDao( final MixinDao value )
    {
        this.mixinDao = value;
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
