package com.enonic.wem.api.content.type.form;

import java.util.HashMap;
import java.util.Map;

public class MockMixinFetcher
    implements MixinFetcher
{
    private Map<QualifiedMixinName, Mixin> mixinMap = new HashMap<QualifiedMixinName, Mixin>();

    @Override
    public Mixin getMixin( final QualifiedMixinName qualifiedName )
    {
        return mixinMap.get( qualifiedName );
    }

    public void add( final FormItemSetMixin mixin )
    {
        mixinMap.put( mixin.getQualifiedName(), mixin );
    }

    public void add( final InputMixin mixin )
    {
        mixinMap.put( mixin.getQualifiedName(), mixin );
    }
}
