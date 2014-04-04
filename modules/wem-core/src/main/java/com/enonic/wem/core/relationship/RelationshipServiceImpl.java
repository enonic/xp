package com.enonic.wem.core.relationship;


import javax.inject.Inject;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.core.relationship.dao.RelationshipDao;


public class RelationshipServiceImpl
    implements RelationshipService
{
    private RelationshipDao relationshipDao;

    private ContentTypeService contentTypeService;

    @Override
    public void syncRelationships( final SyncRelationshipsCommand command )
    {
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( ContentTypeNames.from( command.getContentType() ) );
        final ContentType contentType = contentTypeService.getByNames( params ).first();

        final SyncRelationships syncRelationships =
            new SyncRelationships( contentType.form(), command.getContentToUpdate(), command.getContentBeforeEditing(),
                                   command.getContentAfterEditing() );
        syncRelationships.invoke();

        for ( RelationshipKey relationshipToDelete : syncRelationships.getRelationshipsToDelete() )
        {
            relationshipDao.delete( relationshipToDelete, command.getJcrSession() );
        }
        for ( Relationship relationshipToAdd : syncRelationships.getRelationshipsToAdd() )
        {
            relationshipDao.create( relationshipToAdd, command.getJcrSession() );
        }
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
