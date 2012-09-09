package com.enonic.wem.web.rest.rpc.account;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;

class GetAccountJsonResult
    extends AbstractAccountJsonResult
{
    protected Account account;

    protected List<Account> members;

    protected List<Account> memberships;

    public GetAccountJsonResult( Account account, List<Account> members, List<Account> memberships )
    {
        super();
        this.account = account;
        this.members = members;
        this.memberships = memberships;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        serializeAccount( json, account );
        if ( account instanceof UserAccount )
        {
            json.put( "memberships", serialize( memberships ) );
        }
        else
        {
            json.put( "members", serialize( members ) );
        }
    }

    private ArrayNode serialize( final List<Account> accounts )
    {
        ArrayNode jsons = arrayNode();
        if ( accounts != null )
        {
            for ( Account account : accounts )
            {
                serializeAccount( jsons.addObject(), account );
            }
        }
        return jsons;
    }
}
