package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.UpdateSubTypes;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.editor.SubTypeEditor;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.SubTypeDao;

@Component
public final class UpdateSubTypesHandler
    extends CommandHandler<UpdateSubTypes>
{
    private SubTypeDao subTypeDao;

    public UpdateSubTypesHandler()
    {
        super( UpdateSubTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateSubTypes command )
        throws Exception
    {
        final QualifiedSubTypeNames qualifiedSubTypeNames = command.getNames();
        final SubTypeEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        int subTypesUpdated = 0;
        for ( QualifiedSubTypeName qualifiedSubTypeName : qualifiedSubTypeNames )
        {
            final SubType subType = retrieveSubType( session, qualifiedSubTypeName );
            if ( subType != null )
            {
                final SubType modifiedSubType = editor.edit( subType );
                if ( modifiedSubType != null )
                {
                    updateSubType( session, subType );
                    subTypesUpdated++;
                }
            }
        }

        session.save();
        command.setResult( subTypesUpdated );
    }

    private void updateSubType( final Session session, final SubType subType )
    {
        subTypeDao.updateSubType( subType, session );
    }

    private SubType retrieveSubType( final Session session, final QualifiedSubTypeName contentTypeName )
    {
        final SubTypes contentTypes = subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( contentTypeName ), session );
        return contentTypes.isEmpty() ? null : contentTypes.first();
    }

    @Autowired
    public void setSubTypeDao( final SubTypeDao subTypeDao )
    {
        this.subTypeDao = subTypeDao;
    }
}
