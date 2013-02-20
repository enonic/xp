package com.enonic.wem.core.content.schema.mixin.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.jcr.JcrConstants;

public interface MixinDao
{
    public static final String MIXINS_NODE = "mixins";

    public static final String MIXINS_PATH = JcrConstants.ROOT_NODE + "/" + MIXINS_NODE + "/";

    public void create( Mixin mixin, Session session );

    public void update( Mixin mixin, Session session );

    public void delete( QualifiedMixinName qualifiedMixinName, Session session );

    public Mixins selectAll( Session session );

    public Mixins select( QualifiedMixinNames qualifiedMixinNames, Session session );
}
