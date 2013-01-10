package com.enonic.wem.core.content.type.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.core.jcr.JcrConstants;

public interface MixinDao
{
    public static final String MIXINS_NODE = "mixins";

    public static final String MIXINS_PATH = JcrConstants.ROOT_NODE + "/" + MIXINS_NODE + "/";

    public void createMixin( Mixin mixin, Session session );

    public void updateMixin( Mixin mixin, Session session );

    public void deleteMixin( QualifiedMixinName qualifiedMixinName, Session session );

    public Mixins retrieveAllMixins( Session session );

    public Mixins retrieveMixins( QualifiedMixinNames qualifiedMixinNames, Session session );
}
