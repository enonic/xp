package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.content.relationship.Relationships;
import com.enonic.wem.core.content.dao.ContentDao;


public interface RelationshipDao
{
    public static final String RELATIONSHIPS_NODE = ContentDao.NON_CONTENT_NODE_PREFIX + "relationships";

    public static final String TO_CONTENT_NODE_PREFIX = "toContent_";

    public static final String MANAGING_DATA_NODE = "wem:managingData";

    public RelationshipId create( final Relationship relationship, final Session session );

    public void update( final Relationship relationship, final Session session )
        throws RelationshipNotFoundException;

    public void delete( final RelationshipId relationshipId, final Session session )
        throws RelationshipNotFoundException;

    public void delete( final RelationshipKey relationshipKey, final Session session )
        throws RelationshipNotFoundException;

    public RelationshipIds exists( final RelationshipIds relationshipIds, final Session session );

    public Relationship select( RelationshipKey relationshipKey, final Session session );

    public Relationships selectFromContent( ContentId fromContent, final Session session );

}
