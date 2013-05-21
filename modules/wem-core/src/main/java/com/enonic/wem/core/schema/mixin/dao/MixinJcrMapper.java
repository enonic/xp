package com.enonic.wem.core.schema.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.mixin.MixinJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;

class MixinJcrMapper
{
    private static final String MIXIN = "mixin";

    private static final String CREATED_TIME = "createdTime";

    private static final String MODIFIED_TIME = "modifiedTime";

    private final MixinJsonSerializer jsonSerializer = new MixinJsonSerializer();

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final Mixin mixin, final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = jsonSerializer.toString( mixin );
        mixinNode.setProperty( MIXIN, mixinJson );
        setPropertyDateTime( mixinNode, CREATED_TIME, mixin.getCreatedTime() );
        setPropertyDateTime( mixinNode, MODIFIED_TIME, mixin.getModifiedTime() );
        iconJcrMapper.toJcr( mixin.getIcon(), mixinNode );
    }

    Mixin toMixin( final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = mixinNode.getProperty( MIXIN ).getString();
        return newMixin( jsonSerializer.toObject( mixinJson ) ).
            createdTime( getPropertyDateTime( mixinNode, CREATED_TIME ) ).
            modifiedTime( getPropertyDateTime( mixinNode, MODIFIED_TIME ) ).
            icon( iconJcrMapper.toIcon( mixinNode ) ).
            build();
    }

}
