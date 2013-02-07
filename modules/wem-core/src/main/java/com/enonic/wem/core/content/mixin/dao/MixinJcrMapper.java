package com.enonic.wem.core.content.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.content.mixin.MixinJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.content.mixin.Mixin.newMixin;

class MixinJcrMapper
{
    private static final String MIXIN = "mixin";

    private final MixinJsonSerializer jsonSerializer = new MixinJsonSerializer();

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final Mixin mixin, final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = jsonSerializer.toString( mixin );
        mixinNode.setProperty( MIXIN, mixinJson );
        iconJcrMapper.toJcr( mixin.getIcon(), mixinNode );
    }

    Mixin toMixin( final Node mixinNode )
        throws RepositoryException
    {
        final String mixinJson = mixinNode.getProperty( MIXIN ).getString();
        final Mixin mixin = jsonSerializer.toObject( mixinJson );
        final Icon icon = iconJcrMapper.toIcon( mixinNode );
        return icon == null ? mixin : newMixin( mixin ).icon( icon ).build();
    }

}
