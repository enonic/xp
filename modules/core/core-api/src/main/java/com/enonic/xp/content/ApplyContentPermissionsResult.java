package com.enonic.xp.content;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyContentPermissionsResult
{
    private final Map<ContentId, AccessControlList> results;

    private ApplyContentPermissionsResult( Builder builder )
    {
        this.results = Collections.unmodifiableMap( builder.results );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<ContentId, AccessControlList> getResults()
    {
        return results;
    }

    public AccessControlList getResult( final ContentId contentId )
    {
        return this.results.get( contentId );
    }

    public static final class Builder
    {
        private final Map<ContentId, AccessControlList> results = new HashMap<>();

        private Builder()
        {
        }

        public Builder addResult( ContentId contentId, AccessControlList permissions )
        {
            results.put( contentId, permissions );
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
