package com.enonic.wem.core.content.schema.mixin;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.mixin.UpdateMixins;
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
public final class UpdateMixinsHandler
    extends CommandHandler<UpdateMixins>
{
    private MixinDao mixinDao;

    public UpdateMixinsHandler()
    {
        super( UpdateMixins.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateMixins command )
        throws Exception
    {
        final QualifiedMixinNames qualifiedMixinNames = command.getQualifiedNames();
        final MixinEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        int mixinsUpdated = 0;
        for ( QualifiedMixinName qualifiedMixinName : qualifiedMixinNames )
        {
            final Mixin mixin = retrieveMixin( session, qualifiedMixinName );
            if ( mixin != null )
            {
                final Mixin modifiedMixin = editor.edit( mixin );
                if ( modifiedMixin != null )
                {
                    updateMixin( session, modifiedMixin );
                    mixinsUpdated++;
                }
            }
        }

        session.save();
        command.setResult( mixinsUpdated );
    }

    private void updateMixin( final Session session, final Mixin mixin )
    {
        final Mixin mixinModified = newMixin( mixin ).modifiedTime( DateTime.now() ).build();
        mixinDao.update( mixinModified, session );
    }

    private Mixin retrieveMixin( final Session session, final QualifiedMixinName qualifiedMixinName )
    {
        final Mixins mixins = mixinDao.select( QualifiedMixinNames.from( qualifiedMixinName ), session );
        return mixins.isEmpty() ? null : mixins.first();
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
