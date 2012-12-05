package com.enonic.wem.core.content.type.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;

public interface SubTypeDao
{
    public void createSubType( SubType subType, Session session );

    public void updateSubType( SubType subType, Session session );

    public void deleteSubType( QualifiedSubTypeName subTypeName, Session session );

    public SubTypes retrieveAllSubTypes( Session session );

    public SubTypes retrieveSubTypes( QualifiedSubTypeNames qualifiedSubTypeNames, Session session );
}
