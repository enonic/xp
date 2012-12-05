package com.enonic.wem.core.content.type.dao;


import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;

@Component
public class SubTypeDaoImpl
    implements SubTypeDao
{

    @Override
    public void createSubType( final SubType subType, final Session session )
    {
        // TODO
    }

    @Override
    public void updateSubType( final SubType subType, final Session session )
    {
        // TODO
    }

    @Override
    public void deleteSubType( final QualifiedSubTypeName subTypeName, final Session session )
    {
        // TODO
    }

    @Override
    public SubTypes retrieveAllSubTypes( final Session session )
    {
        return null; // TODO
    }

    @Override
    public SubTypes retrieveSubTypes( final QualifiedSubTypeNames qualifiedSubTypeNames, Session session )
    {
        List<SubType> subTypeList = new ArrayList<SubType>();
        for ( QualifiedSubTypeName qName : qualifiedSubTypeNames )
        {
            // TODO:
        }

        return SubTypes.from( subTypeList );
    }
}
