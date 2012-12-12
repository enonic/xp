package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.GetSubTypes;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.SubTypeDao;

@Component
public final class GetSubTypesHandler
    extends CommandHandler<GetSubTypes>
{
    private SubTypeDao subTypeDao;

    public GetSubTypesHandler()
    {
        super( GetSubTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetSubTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final SubTypes subTypes;
        if ( command.isGetAll() )
        {
            subTypes = getAllSubTypes( session );
        }
        else
        {
            final QualifiedSubTypeNames qualifiedNames = command.getQualifiedSubTypeNames();
            subTypes = getContentTypes( qualifiedNames, session );
        }

        command.setResult( subTypes );
    }

    private SubTypes getAllSubTypes( final Session session )
    {
        return subTypeDao.retrieveAllSubTypes( session );
    }

    private SubTypes getContentTypes( final QualifiedSubTypeNames contentTypeNames, final Session session )
    {
        return subTypeDao.retrieveSubTypes( contentTypeNames, session );
    }

    @Autowired
    public void setSubTypeDao( final SubTypeDao subTypeDao )
    {
        this.subTypeDao = subTypeDao;
    }
}
