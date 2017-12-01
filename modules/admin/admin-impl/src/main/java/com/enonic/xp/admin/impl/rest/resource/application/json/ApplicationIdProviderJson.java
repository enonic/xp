package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStores;

public class ApplicationIdProviderJson
{
    private String mode;

    private ImmutableList<ApplicationUserStoreJson> userStores;

    public ApplicationIdProviderJson( final AuthDescriptor authDescriptor, final UserStores userStores )
    {
        if(authDescriptor != null)
        {
            this.mode = authDescriptor.getMode().toString();
        }

        if(userStores != null)
        {
            final ImmutableList.Builder<ApplicationUserStoreJson> builder = ImmutableList.builder();
            if ( userStores != null )
            {
                for ( final UserStore userStore : userStores )
                {
                    builder.add( new ApplicationUserStoreJson( userStore ) );
                }
            }
            this.userStores = builder.build();
        }
    }

    public String getMode()
    {
        return mode;
    }

    public ImmutableList<ApplicationUserStoreJson> getUserStores()
    {
        return userStores;
    }

    public class ApplicationUserStoreJson
    {
        private String displayName;

        private String path;

        public ApplicationUserStoreJson( final UserStore userStore )
        {
            this.displayName = userStore.getDisplayName();
            this.path = "/" + userStore.getKey().toString();
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public String getPath()
        {
            return path;
        }
    }
}
