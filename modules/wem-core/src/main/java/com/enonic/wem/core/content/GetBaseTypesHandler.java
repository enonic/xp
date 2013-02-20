package com.enonic.wem.core.content;

import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.GetBaseTypes;
import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.api.content.BaseTypeKind;
import com.enonic.wem.api.content.BaseTypes;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipTypes;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

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

        final List<BaseType> baseTypeList = Lists.newArrayList();
        if ( command.isIncludeType( BaseTypeKind.CONTENT_TYPE ) )
        {
            final ContentTypes contentTypes = contentTypeDao.selectAll( session );
            Iterables.addAll( baseTypeList, contentTypes );
        }

        if ( command.isIncludeType( BaseTypeKind.MIXIN ) )
        {
            final Mixins mixins = mixinDao.selectAll( session );
            Iterables.addAll( baseTypeList, mixins );
        }

        if ( command.isIncludeType( BaseTypeKind.RELATIONSHIP_TYPE ) )
        {
            final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );
            Iterables.addAll( baseTypeList, relationshipTypes );
        }

        final BaseTypes baseTypes = BaseTypes.from( baseTypeList );

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
