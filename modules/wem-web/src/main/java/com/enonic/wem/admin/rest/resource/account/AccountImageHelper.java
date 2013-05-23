package com.enonic.wem.admin.rest.resource.account;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.elasticsearch.common.collect.Maps;

import com.enonic.wem.admin.rest.resource.BaseImageHelper;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;

final class AccountImageHelper
    extends BaseImageHelper
{
    private final Map<String, BufferedImage> cache;

    public AccountImageHelper()
        throws Exception
    {
        this.cache = Maps.newHashMap();
        populate( "admin" );
        populate( "anonymous" );
        populate( "user" );
        populate( "role" );
        populate( "group" );
    }

    private void populate( final String name )
        throws Exception
    {
        this.cache.put( name, loadDefaultImage( name ) );
    }

    public BufferedImage getDefaultImage( final String key, final int size )
        throws Exception
    {
        final BufferedImage image = this.cache.get( key );
        if ( image == null )
        {
            return null;
        }

        return resizeImage( image, size );
    }

    public BufferedImage getAccountImage( final Account account, final int size )
        throws Exception
    {
        if ( account == null )
        {
            return null;
        }

        if ( !( account instanceof UserAccount ) )
        {
            return null;
        }

        final UserAccount user = (UserAccount) account;
        if ( user.getImage() == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( user.getImage() );
        return resizeImage( image, size );
    }

}
