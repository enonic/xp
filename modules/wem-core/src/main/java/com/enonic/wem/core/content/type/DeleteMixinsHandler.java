package com.enonic.wem.core.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.DeleteMixins;
import com.enonic.wem.api.content.type.MixinDeletionResult;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.MixinDao;

@Component
public final class DeleteMixinsHandler
    extends CommandHandler<DeleteMixins>
{
    private MixinDao mixinDao;

    //private ContentTypeDao contentTypeDao;

    public DeleteMixinsHandler()
    {
        super( DeleteMixins.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteMixins command )
        throws Exception
    {
        final MixinDeletionResult mixinDeletionResult = new MixinDeletionResult();

        for ( QualifiedMixinName qualifiedMixinName : command.getNames() )
        {
            try
            {
                /* TODO: if ( contentTypeDao.countMixinUsage( qualifiedMixinName, context.getJcrSession() ) > 0 )
                {
                    Exception e = new UnableToDeleteMixinException( qualifiedMixinName, "Mixin is being used." );
                    mixinDeletionResult.failure( qualifiedMixinName, e );
                }
                else
                {*/
                mixinDao.delete( qualifiedMixinName, context.getJcrSession() );
                mixinDeletionResult.success( qualifiedMixinName );
                context.getJcrSession().save();
                //}
            }
            catch ( MixinNotFoundException e )
            {
                mixinDeletionResult.failure( qualifiedMixinName, e );
            }
        }

        command.setResult( mixinDeletionResult );
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }

    /*@Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }*/
}
