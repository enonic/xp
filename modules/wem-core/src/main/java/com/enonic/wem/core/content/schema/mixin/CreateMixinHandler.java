package com.enonic.wem.core.content.schema.mixin;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.mixin.CreateMixin;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;

@Component
public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    public CreateMixinHandler()
    {
        super( CreateMixin.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateMixin command )
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

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
}
