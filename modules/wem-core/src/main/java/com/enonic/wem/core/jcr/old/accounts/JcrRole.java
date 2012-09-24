package com.enonic.wem.core.jcr.old.accounts;

public class JcrRole
    extends JcrGroup
    implements JcrAccount
{

    public JcrRole()
    {
        super( JcrAccountType.ROLE );
    }

}
