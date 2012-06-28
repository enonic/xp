package com.enonic.wem.core.jcr;

import com.enonic.wem.core.jcr.accounts.JcrAccount;
import com.enonic.wem.core.jcr.accounts.JcrGroup;
import com.enonic.wem.core.jcr.accounts.JcrUser;

public interface AccountJcrDao
{
    JcrUser findUserById( String accountId );

    JcrGroup findGroupById( String accountId );

    PageList<JcrAccount> findAll( int index, int count, String query, String order );

    byte[] findUserPhotoByKey( String accountId );
}
