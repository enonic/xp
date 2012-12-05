package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.SubTypeDao;

@Component
public final class CreateSubTypeHandler
    extends CommandHandler<CreateSubType>
{
    private SubTypeDao subTypeDao;

    public CreateSubTypeHandler()
    {
        super( CreateSubType.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateSubType command )
        throws Exception
    {
        final SubType subType = command.getSubType();
        final Session session = context.getJcrSession();
        subTypeDao.createSubType( subType, session );
        session.save();
        command.setResult( subType.getQualifiedName() );
    }

    @Autowired
    public void setSubTypeDao( final SubTypeDao value )
    {
        this.subTypeDao = value;
    }
}
