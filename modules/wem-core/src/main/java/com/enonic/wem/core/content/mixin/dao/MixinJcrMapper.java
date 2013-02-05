package com.enonic.wem.core.content.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.content.mixin.MixinJsonSerializer;

class MixinJcrMapper
{
    private static final String MIXIN = "mixin";

    private MixinJsonSerializer jsonSerializer = new MixinJsonSerializer();

    void toJcr( final Mixin mixin, final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = jsonSerializer.toString( mixin );
        mixinNode.setProperty( MIXIN, mixinJson );
    }

    Mixin toMixin( final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = mixinNode.getProperty( MIXIN ).getString();
        return jsonSerializer.toObject( mixinJson );
    }

}
