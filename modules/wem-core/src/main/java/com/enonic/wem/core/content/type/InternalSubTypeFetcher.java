package com.enonic.wem.core.content.type;


import javax.jcr.Session;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.content.type.form.SubTypeFetcher;
import com.enonic.wem.core.content.type.dao.SubTypeDao;

public class InternalSubTypeFetcher
    implements SubTypeFetcher
{
    private final SubTypeDao subTypeDao;

    private final Session session;

    public InternalSubTypeFetcher( final SubTypeDao subTypeDao, final Session session )
    {
        this.subTypeDao = subTypeDao;
        this.session = session;
    }

    @Override
    public SubType getSubType( final QualifiedSubTypeName qualifiedName )
    {
        subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( qualifiedName ), session );
        return null;
    }
}
