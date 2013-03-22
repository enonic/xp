package com.enonic.wem.core.content.schema.mixin;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.content.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.schema.mixin.editor.MixinEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;

@Component
public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private MixinDao mixinDao;

    public UpdateMixinHandler()
    {
        super( UpdateMixin.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateMixin command )
        throws Exception
    {
        final QualifiedMixinName qualifiedMixinName = command.getQualifiedName();
        final MixinEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        final Mixin mixin = retrieveMixin( session, qualifiedMixinName );
        if ( mixin == null )
        {
            command.setResult( UpdateMixinResult.NOT_FOUND );
        }
        else
        {
            final Mixin modifiedMixin = editor.edit( mixin );
            if ( modifiedMixin != null )
            {
                updateMixin( session, modifiedMixin );
                session.save();
                command.setResult( UpdateMixinResult.SUCCESS );
            }
            command.setResult( UpdateMixinResult.SUCCESS );
        }
    }

    private void updateMixin( final Session session, final Mixin mixin )
    {
        final Mixin mixinModified = newMixin( mixin ).modifiedTime( DateTime.now() ).build();
        mixinDao.update( mixinModified, session );
    }

    private Mixin retrieveMixin( final Session session, final QualifiedMixinName qualifiedMixinName )
    {
        final Mixins mixins = mixinDao.select( QualifiedMixinNames.from( qualifiedMixinName ), session );
        return mixins.first();
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
