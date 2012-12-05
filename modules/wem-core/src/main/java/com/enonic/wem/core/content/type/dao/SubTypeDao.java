package com.enonic.wem.core.content.type.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.jcr.JcrConstants;

public interface SubTypeDao
{
    public static final String SUB_TYPES_NODE = "subTypes";

    public static final String SUB_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + SUB_TYPES_NODE + "/";

    public void createSubType( SubType subType, Session session );

    public void updateSubType( SubType subType, Session session );

    public void deleteSubType( QualifiedSubTypeName qualifiedSubTypeName, Session session );

    public SubTypes retrieveAllSubTypes( Session session );

    public SubTypes retrieveSubTypes( QualifiedSubTypeNames qualifiedSubTypeNames, Session session );
}
