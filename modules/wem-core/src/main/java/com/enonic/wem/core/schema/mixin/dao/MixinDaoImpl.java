package com.enonic.wem.core.schema.mixin.dao;

import javax.inject.Inject;

import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

public final class MixinDaoImpl
    implements MixinDao
{
    private SchemaRegistry schemaRegistry;

    @Override
    public Mixins getAllMixins()
    {
        return this.schemaRegistry.getAllMixins();
    }

    @Override
    public Mixin.Builder getMixin( final MixinName mixinName )
    {
        final Mixin mixin = this.schemaRegistry.getMixin( mixinName );
        return mixin != null ? Mixin.newMixin( mixin ) : null;
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}
