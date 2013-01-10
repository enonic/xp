package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.UpdateMixins;
import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.editor.MixinEditor;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.MixinDao;

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
        final QualifiedMixinNames qualifiedMixinNames = command.getQualifiedMixinNames();
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
                    updateMixin( session, mixin );
                    mixinsUpdated++;
                }
            }
        }

        session.save();
        command.setResult( mixinsUpdated );
    }

    private void updateMixin( final Session session, final Mixin mixin )
    {
        mixinDao.updateMixin( mixin, session );
    }

    private Mixin retrieveMixin( final Session session, final QualifiedMixinName qualifiedMixinName )
    {
        final Mixins mixins = mixinDao.retrieveMixins( QualifiedMixinNames.from( qualifiedMixinName ), session );
        return mixins.isEmpty() ? null : mixins.first();
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
