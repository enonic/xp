package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.GetBaseTypes;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.api.content.type.BaseTypes;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipTypeDao;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;
import com.enonic.wem.core.content.type.dao.MixinDao;

@Component
public final class GetBaseTypesHandler
    extends CommandHandler<GetBaseTypes>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    public GetBaseTypesHandler()
    {
        super( GetBaseTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetBaseTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final ContentTypes contentTypes = contentTypeDao.selectAll( session );
        final Mixins mixins = mixinDao.selectAll( session );
        final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );

        final BaseTypes baseTypes = BaseTypes.from( contentTypes, mixins, relationshipTypes );

        command.setResult( baseTypes );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
