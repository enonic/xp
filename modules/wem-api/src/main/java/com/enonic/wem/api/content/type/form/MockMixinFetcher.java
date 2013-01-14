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

    public void add( final Mixin mixin )
    {
        mixinMap.put( mixin.getQualifiedName(), mixin );
    }
}
