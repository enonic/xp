package com.enonic.wem.core.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.DeleteSubTypes;
import com.enonic.wem.api.content.type.SubTypeDeletionResult;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.exception.SubTypeNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.SubTypeDao;

@Component
public final class DeleteSubTypesHandler
    extends CommandHandler<DeleteSubTypes>
{
    private SubTypeDao subTypeDao;

    //private ContentTypeDao contentTypeDao;

    public DeleteSubTypesHandler()
    {
        super( DeleteSubTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteSubTypes command )
        throws Exception
    {
        final SubTypeDeletionResult subTypeDeletionResult = new SubTypeDeletionResult();

        for ( QualifiedSubTypeName qualifiedSubTypeName : command.getNames() )
        {
            try
            {
                /* TODO: if ( contentTypeDao.countSubTypeUsage( qualifiedSubTypeName, context.getJcrSession() ) > 0 )
                {
                    Exception e = new UnableToDeleteSubTypeException( qualifiedSubTypeName, "Sub type is being used." );
                    subTypeDeletionResult.failure( qualifiedSubTypeName, e );
                }
                else
                {*/
                subTypeDao.deleteSubType( qualifiedSubTypeName, context.getJcrSession() );
                subTypeDeletionResult.success( qualifiedSubTypeName );
                context.getJcrSession().save();
                //}
            }
            catch ( SubTypeNotFoundException e )
            {
                subTypeDeletionResult.failure( qualifiedSubTypeName, e );
            }
        }

        command.setResult( subTypeDeletionResult );
    }

    @Autowired
    public void setSubTypeDao( final SubTypeDao subTypeDao )
    {
        this.subTypeDao = subTypeDao;
    }

    /*@Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }*/
}
