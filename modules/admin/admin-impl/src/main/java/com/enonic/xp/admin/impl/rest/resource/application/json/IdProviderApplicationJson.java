package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviders;

public class IdProviderApplicationJson
{
    private String mode;

    private ImmutableList<IdProviderJson> idProviders;

    public IdProviderApplicationJson( final IdProviderDescriptor idProviderDescriptor, final IdProviders idProviders )
    {
        if ( idProviderDescriptor != null )
        {
            this.mode = idProviderDescriptor.getMode().toString();
        }

        if ( idProviders != null )
        {
            final ImmutableList.Builder<IdProviderJson> builder = ImmutableList.builder();
            if ( idProviders != null )
            {
                for ( final IdProvider idProvider : idProviders )
                {
                    builder.add( new IdProviderJson( idProvider ) );
                }
            }
            this.idProviders = builder.build();
        }
    }

    public String getMode()
    {
        return mode;
    }

    public ImmutableList<IdProviderJson> getIdProviders()
    {
        return idProviders;
    }

    public class IdProviderJson
    {
        private String displayName;

        private String path;

        public IdProviderJson( final IdProvider idProvider )
        {
            this.displayName = idProvider.getDisplayName();
            this.path = "/" + idProvider.getKey().toString();
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
