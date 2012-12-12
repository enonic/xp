package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.content.type.SubTypeJsonSerializer;

class SubTypeJcrMapper
{
    private static final String SUB_TYPE = "subType";

    private SubTypeJsonSerializer jsonSerializer = new SubTypeJsonSerializer();

    void toJcr( final SubType subType, final Node subTypeNode )
        throws RepositoryException
    {
        final String subTypeJson = jsonSerializer.toString( subType );
        subTypeNode.setProperty( SUB_TYPE, subTypeJson );
    }

    SubType toSubType( final Node subTypeNode )
        throws RepositoryException
    {
        final String subTypeJson = subTypeNode.getProperty( SUB_TYPE ).getString();
        return jsonSerializer.toObject( subTypeJson );
    }

}
