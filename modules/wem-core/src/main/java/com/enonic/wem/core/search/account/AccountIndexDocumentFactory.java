package com.enonic.wem.core.search.account;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

public class AccountIndexDocumentFactory
{
    private AccountIndexDocumentFactory()
    {
    }

    public static Collection<IndexDocument> create( Account account )
    {
        Set<IndexDocument> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createAccountIndexDocument( account ) );

        return indexDocuments;
    }

    private static IndexDocument createAccountIndexDocument( final Account account )
    {
        IndexDocument indexDocument =
            new IndexDocument( account.getKey().toString(), IndexType.ACCOUNT, IndexConstants.WEM_INDEX.string() );

        switch ( account.getKey().getType() )
        {
            case USER:
                appendUser( (UserAccount) account, indexDocument );
                break;
            case ROLE:
            case GROUP:
                appendGroupOrRole( (NonUserAccount) account, indexDocument );
                break;
            default:
                throw new UnsupportedOperationException( "Unable to build index for account of type " + account.getKey().getType() );
        }

        return indexDocument;
    }

    private static void appendUser( final UserAccount user, final IndexDocument indexDocument )
    {
        addAccountMetaData( user, indexDocument );
        addEmail( user, indexDocument );
        addProfile( user, indexDocument );
    }

    private static void appendGroupOrRole( final NonUserAccount noneUserAccount, final IndexDocument indexDocument )
    {
        addAccountMetaData( noneUserAccount, indexDocument );
        addMembers( noneUserAccount, indexDocument );
    }

    private static void addAccountMetaData( final Account account, final IndexDocument indexDocument )
    {
        indexDocument.addDocumentEntry( AccountIndexField.KEY_FIELD.id(), account.getKey().toString(), false, true );
        indexDocument.addDocumentEntry( AccountIndexField.TYPE_FIELD.id(), account.getKey().getType().name(), false, true );
        indexDocument.addDocumentEntry( AccountIndexField.NAME_FIELD.id(), account.getKey().getLocalName(), true, true );
        indexDocument.addDocumentEntry( AccountIndexField.DISPLAY_NAME_FIELD.id(), account.getDisplayName(), true, true );
        indexDocument.addDocumentEntry( AccountIndexField.USERSTORE_FIELD.id(), account.getKey().getUserStore(), false, false );
        indexDocument.addDocumentEntry( AccountIndexField.LAST_MODIFIED_FIELD.id(), account.getModifiedTime(), false, true );
    }

    private static void addEmail( final UserAccount user, final IndexDocument indexDocument )
    {
        indexDocument.addDocumentEntry( AccountIndexField.EMAIL_FIELD.id(), user.getEmail(), true, true );
    }

    private static void addProfile( final UserAccount user, final IndexDocument indexDocument )
    {
        final UserProfile profile = user.getProfile();
        if ( ( profile != null ) && ( profile.getOrganization() != null ) )
        {
            indexDocument.addDocumentEntry( AccountIndexField.ORGANIZATION_FIELD.id(), profile.getOrganization(), true, true );
        }
    }

    private static void addMembers( final NonUserAccount noneUserAccount, final IndexDocument indexDocument )
    {
        final AccountKeys members = noneUserAccount.getMembers() != null ? noneUserAccount.getMembers() : AccountKeys.empty();
        final String[] memberKeys = new String[members.getSize()];
        int i = 0;
        for ( AccountKey member : members )
        {
            memberKeys[i++] = member.toString();
        }
        indexDocument.addDocumentEntry( AccountIndexField.MEMBERS_FIELD.id(), memberKeys, true, true );
    }

}
