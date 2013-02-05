package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;


class RelationshipTypeTreeFactory
{
    private final Session jcrSession;

    private final RelationshipTypeDao relationshipTypeDao;

    RelationshipTypeTreeFactory( final Session jcrSession, final RelationshipTypeDao relationshipTypeDao )
    {
        this.jcrSession = jcrSession;
        this.relationshipTypeDao = relationshipTypeDao;
    }

    Tree<RelationshipType> createTree()
    {
        //relationshipTypeDao.retrieveAllRelationshipTypes( jcrSession );
        return null;
    }
}
