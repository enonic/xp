package com.enonic.xp.admin.widget;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

@Beta
public class GetWidgetDescriptorsParams
{
    private PrincipalKeys principalKeys;

    private ImmutableSet<String> interfaceNames;

    private GetWidgetDescriptorsParams( final Builder builder )
    {
        principalKeys = builder.principalKeys == null ? null : PrincipalKeys.from( builder.principalKeys );
        interfaceNames = builder.interfaceNames == null ? null : ImmutableSet.copyOf( builder.interfaceNames );
    }

    public PrincipalKeys getPrincipalKeys()
    {
        return principalKeys;
    }

    public ImmutableSet<String> getInterfaceNames()
    {
        return interfaceNames;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Collection<PrincipalKey> principalKeys;

        private Collection<String> interfaceNames;

        private Builder()
        {
        }

        public Builder setPrincipalKeys( final Collection<PrincipalKey> principalKeys )
        {
            this.principalKeys = principalKeys;
            return this;
        }

        public Builder setInterfaceNames( final Collection<String> interfaceNames )
        {
            this.interfaceNames = interfaceNames;
            return this;
        }

        public GetWidgetDescriptorsParams build()
        {
            return new GetWidgetDescriptorsParams( this );
        }
    }
}
