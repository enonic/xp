package com.enonic.wem.migrate.jcr;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreConfigParser;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldHelper;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.core.user.field.UserInfoTransformer;

@Component
public class JcrAccountsImporter
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrAccountsImporter.class );

    private DatabaseAccountsLoader dbAccountsLoader;

    private AccountDao accountDao;

    private AccountSearchService accountSearchService;

    private final Map<Integer, String> userStoreKeyName;

    private final Multimap<String, AccountKey> userStoreAdministrators;

    private final Map<String, String> entityKeyToAccountKeyMapping;

    private final Map<AccountKey, Account> accountsImported;

    public JcrAccountsImporter()
    {
        userStoreKeyName = new HashMap<Integer, String>();
        entityKeyToAccountKeyMapping = new HashMap<String, String>();
        userStoreAdministrators = ArrayListMultimap.create();
        accountsImported = Maps.newHashMap();
    }

    public void importAccounts( final Session session )
    {
        importUserStores( session );

        importUsers( session );

        importGroups( session );

        setUserStoreAdministrators( session );

        indexAccounts();

        releaseResources();
    }

    private void setUserStoreAdministrators( final Session session )
    {
        for ( String userStoreName : userStoreAdministrators.keys() )
        {
            final Collection<AccountKey> administrators = userStoreAdministrators.get( userStoreName );
            try
            {
                accountDao.setUserStoreAdministrators( session, UserStoreName.from( userStoreName ), AccountKeys.from( administrators ) );
            }
            catch ( Exception e )
            {
                LOG.error( "Unable to set administrators for user store " + userStoreName, e );
            }
        }
    }

    private void importUserStores( final Session session )
    {
        dbAccountsLoader.loadUserStores( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> userStoreFields )
            {
                try
                {
                    importUserStore( session, userStoreFields );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import user store", e );
                }
            }
        } );
    }

    private void importUsers( final Session session )
    {
        dbAccountsLoader.loadUsers( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                try
                {
                    importUser( session, data );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import user: " + data.get( "USR_SUID" ), e );
                }
            }
        } );
    }

    private void importGroups( final Session session )
    {
        dbAccountsLoader.loadGroups( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                try
                {
                    importGroup( session, data );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import group: " + data.get( "GRP_SNAME" ), e );
                }
            }
        } );

        final Map<AccountKey, AccountKeys> membershipsTable = Maps.newHashMap();
        dbAccountsLoader.loadMemberships( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                final String memberKey = (String) data.get( "GGM_MBR_GRP_HKEY" );
                final String accountKey = (String) data.get( "GGM_GRP_HKEY" );

                final String group = entityKeyToAccountKeyMapping.get( accountKey );
                final String member = entityKeyToAccountKeyMapping.get( memberKey );
                if ( member == null || group == null )
                {
                    return;
                }

                final AccountKey groupAccountKey = AccountKey.from( group );
                AccountKeys groupMembers = membershipsTable.get( groupAccountKey );
                if ( groupMembers == null )
                {
                    groupMembers = AccountKeys.empty();
                }
                groupMembers = groupMembers.add( member );
                membershipsTable.put( groupAccountKey, groupMembers );
            }
        } );
        for ( AccountKey accountKey : membershipsTable.keySet() )
        {
            try
            {
                setMembers( session, accountKey, membershipsTable.get( accountKey ) );
            }
            catch ( Exception e )
            {
                LOG.error( "Unable to set members for: " + accountKey.toString(), e );
            }
        }
    }

    private void indexAccounts()
    {
        for ( Account account : accountsImported.values() )
        {
            accountSearchService.index( account );
        }
    }

    private void setMembers( final Session session, final AccountKey nonUserAccountKey, final AccountKeys members )
        throws Exception
    {
        accountDao.setMembers( session, nonUserAccountKey, members );
        final NonUserAccount nonUserAccount = (NonUserAccount) accountsImported.get( nonUserAccountKey );
        nonUserAccount.setMembers( members );
        LOG.info( "Set account members for " + nonUserAccountKey.toString() + ": " + members.toString() );
    }

    private void releaseResources()
    {
        userStoreKeyName.clear();
        entityKeyToAccountKeyMapping.clear();
        userStoreAdministrators.clear();
        accountsImported.clear();
    }

    private void importGroup( final Session session, final Map<String, Object> groupFields )
        throws Exception
    {
        final String groupName = (String) groupFields.get( "GRP_SNAME" );
        final GroupType groupType = GroupType.get( (Integer) groupFields.get( "GRP_LTYPE" ) );
        if ( groupType == GroupType.USER )
        {
            LOG.debug( "Skipping group of type User: " + groupName );
            return;
        }
        final Integer userStoreKey = (Integer) groupFields.get( "GRP_DOM_LKEY" );
        String userStoreName = userStoreKeyName.get( userStoreKey );
        if ( userStoreName == null )
        {
            userStoreName = UserStoreName.system().toString();
        }
        final DateTime lastModified = new DateTime();

        final NonUserAccount nonUserAccount;
        if ( groupType.isBuiltIn() && ( userStoreName.equals( UserStoreName.system().toString() ) ) )
        {
            nonUserAccount = RoleAccount.create( userStoreName + ":" + groupName );
        }
        else
        {
            nonUserAccount = GroupAccount.create( userStoreName + ":" + groupName );
        }

        nonUserAccount.setDisplayName( groupName );
        nonUserAccount.setModifiedTime( lastModified );
        nonUserAccount.setCreatedTime( lastModified );

        if ( nonUserAccount instanceof GroupAccount )
        {
            accountDao.createGroup( session, (GroupAccount) nonUserAccount );
        }
        else
        {
            accountDao.createRole( session, (RoleAccount) nonUserAccount );
        }

        accountsImported.put( nonUserAccount.getKey(), nonUserAccount );

        final String groupId = nonUserAccount.getKey().toString();
        final String groupKey = (String) groupFields.get( "GRP_HKEY" );
        entityKeyToAccountKeyMapping.put( groupKey, groupId );

        if ( groupType == GroupType.USERSTORE_ADMINS )
        {
            userStoreAdministrators.put( userStoreName, nonUserAccount.getKey() );
        }

        LOG.info( "Group '" + groupName + "' imported with id " + groupId );
    }

    private void importUser( final Session session, final Map<String, Object> userFields )
        throws Exception
    {
        Integer userStoreKey = (Integer) userFields.get( "USR_DOM_LKEY" );
        String userStoreName = userStoreKeyName.get( userStoreKey );
        if ( userStoreName == null )
        {
            userStoreName = UserStoreName.system().toString();
        }
        final String userName = (String) userFields.get( "USR_SUID" );
        final String displayName = (String) userFields.get( "USR_SFULLNAME" );
        final String email = (String) userFields.get( "USR_SEMAIL" );
        final Date lastModified = (Date) userFields.get( "USR_DTETIMESTAMP" );
        final byte[] photo = (byte[]) userFields.get( "USR_PHOTO" );
        final String key = (String) userFields.get( "USR_HKEY" );
        final String syncValue = (String) userFields.get( "USR_SSYNCVALUE" );
        final UserType userType = UserType.getByKey( (Integer) userFields.get( "USR_UT_LKEY" ) );

        final UserAccount user = UserAccount.create( userStoreName + ":" + userName );
        user.setDisplayName( displayName );
        user.setEmail( email );
        user.setModifiedTime( new DateTime( lastModified ) );
        user.setCreatedTime( new DateTime( lastModified ) );
        user.setImage( photo );

        // user info fields
        final Map<String, Object> userInfoFields = (Map<String, Object>) userFields.get( DatabaseAccountsLoader.USER_INFO_FIELDS_MAP );
        addUserInfoFields( user, userInfoFields );

        accountDao.createUser( session, user );

        accountsImported.put( user.getKey(), user );

        final String userId = user.getKey().toString();
        final String userKey = (String) userFields.get( "USR_GRP_HKEY" );
        entityKeyToAccountKeyMapping.put( userKey, userId );
        LOG.info( "User '" + userName + "' imported with id " + userId );
    }

    private void importUserStore( final Session session, final Map<String, Object> userStoreFields )
        throws Exception
    {
        final String userStoreName = (String) userStoreFields.get( "DOM_SNAME" );
        final Integer key = (Integer) userStoreFields.get( "DOM_LKEY" );
        final boolean defaultUserStore = ( (Integer) userStoreFields.get( "DOM_BDEFAULTSTORE" ) == 1 );
        final String connectorName = (String) userStoreFields.get( "DOM_SCONFIGNAME" );
        final byte[] xmlBytes = (byte[]) userStoreFields.get( "DOM_XMLDATA" );
        final String userStoreXmlConfig = new String( xmlBytes, "UTF-8" );

        final UserStore userStore = new UserStore( UserStoreName.from( userStoreName ) );
        userStore.setDefaultStore( defaultUserStore );
        final UserStoreConfig config;
        if ( Strings.isNullOrEmpty( userStoreXmlConfig ) )
        {
            config = new UserStoreConfig();
        }
        else
        {
            config = new UserStoreConfigParser().parseXml( userStoreXmlConfig );
        }
        userStore.setConfig( config );
        userStore.setConnectorName( connectorName );
        if ( userStore.getName().isSystem() )
        {
            accountDao.updateUserStore( session, userStore );
        }
        else
        {
            accountDao.createUserStore( session, userStore );
        }

        userStoreKeyName.put( key, userStoreName );
        LOG.info( "User store imported: " + userStoreName );
    }

    private void addUserInfoFields( final UserAccount user, final Map<String, Object> userInfoFields )
    {
        final UserFieldHelper userFieldHelper = new UserFieldHelper();
        final UserFields userFields = new UserFields( true );
        for ( String userFieldName : userInfoFields.keySet() )
        {
            final UserFieldType type = UserFieldType.fromName( userFieldName );
            if ( type != null )
            {
                final Object value = userFieldHelper.fromString( type, userInfoFields.get( userFieldName ).toString() );
                final UserField field = new UserField( type, value );
                userFields.add( field );
            }
        }
        final UserInfoTransformer transformer = new UserInfoTransformer();
        final UserInfo userInfo = transformer.toUserInfo( userFields );

        final Address[] addresses = userFieldsToAddresses( userInfoFields );

        userInfo.setAddresses( addresses );

        userInfoFieldsToUserProfile( userInfo, user );
    }

    private Address[] userFieldsToAddresses( final Map<String, Object> userFields )
    {
        final Map<String, Address> addresses = new HashMap<String, Address>();
        for ( String fieldName : userFields.keySet() )
        {
            if ( fieldName.startsWith( "address[" ) )
            {
                final String addressId = StringUtils.substringBetween( fieldName, "address[", "]" );
                Address address = addresses.get( addressId );
                if ( address == null )
                {
                    address = new Address();
                    addresses.put( addressId, address );
                }
                final String fieldId = StringUtils.substringAfter( fieldName, "." );
                final String value = (String) userFields.get( fieldName );
                if ( "label".equals( fieldId ) )
                {
                    address.setLabel( value );
                }
                else if ( "country".equals( fieldId ) )
                {
                    address.setCountry( value );
                }
                else if ( "iso-country".equals( fieldId ) )
                {
                    address.setIsoCountry( value );
                }
                else if ( "region".equals( fieldId ) )
                {
                    address.setRegion( value );
                }
                else if ( "iso-region".equals( fieldId ) )
                {
                    address.setIsoRegion( value );
                }
                else if ( "street".equals( fieldId ) )
                {
                    address.setStreet( value );
                }
                else if ( "postal-code".equals( fieldId ) )
                {
                    address.setPostalCode( value );
                }
                else if ( "postal-address".equals( fieldId ) )
                {
                    address.setPostalAddress( value );
                }
            }
        }
        return addresses.values().toArray( new Address[addresses.size()] );
    }

    private void userInfoFieldsToUserProfile( final UserInfo userInfo, final UserAccount user )
    {
        final UserProfile userProfile = new UserProfile();
        if ( userInfo.getBirthday() != null )
        {
            userProfile.setBirthday( new DateTime( userInfo.getBirthday() ) );
        }
        userProfile.setCountry( userInfo.getCountry() );
        userProfile.setDescription( userInfo.getDescription() );
        userProfile.setFax( userInfo.getFax() );
        userProfile.setFirstName( userInfo.getFirstName() );
        userProfile.setGlobalPosition( userInfo.getGlobalPosition() );
        userProfile.setHomePage( userInfo.getHomePage() );
        userProfile.setHtmlEmail( userInfo.getHtmlEmail() );
        userProfile.setInitials( userInfo.getInitials() );
        userProfile.setLastName( userInfo.getLastName() );
        final Locale locale = userInfo.getLocale();
        if ( locale != null )
        {
            userProfile.setLocale( locale );
        }
        userProfile.setMemberId( userInfo.getMemberId() );
        userProfile.setMiddleName( userInfo.getMiddleName() );
        userProfile.setMobile( userInfo.getMobile() );
        userProfile.setOrganization( userInfo.getOrganization() );
        userProfile.setPersonalId( userInfo.getPersonalId() );
        userProfile.setPhone( userInfo.getPhone() );
        userProfile.setPrefix( userInfo.getPrefix() );
        userProfile.setSuffix( userInfo.getSuffix() );
        final TimeZone timezone = userInfo.getTimeZone();
        if ( timezone != null )
        {
            userProfile.setTimeZone( timezone );
        }
        userProfile.setTitle( userInfo.getTitle() );
        final Gender gender = userInfo.getGender();
        if ( gender != null )
        {
            userProfile.setGender( com.enonic.wem.api.account.profile.Gender.valueOf( gender.toString() ) );
        }
        userProfile.setOrganization( userInfo.getOrganization() );

        final Address[] addresses = userInfo.getAddresses();
        for ( Address address : addresses )
        {
            addProfileAddress( address, userProfile );
        }
        user.setProfile( userProfile );
    }

    private void addProfileAddress( final Address address, final UserProfile profile )
    {
        final com.enonic.wem.api.account.profile.Address profileAddress = new com.enonic.wem.api.account.profile.Address();
        profileAddress.setCountry( address.getCountry() );
        profileAddress.setIsoCountry( address.getIsoCountry() );
        profileAddress.setIsoRegion( address.getIsoRegion() );
        profileAddress.setLabel( address.getLabel() );
        profileAddress.setPostalAddress( address.getPostalAddress() );
        profileAddress.setPostalCode( address.getPostalCode() );
        profileAddress.setRegion( address.getRegion() );
        profileAddress.setStreet( address.getStreet() );

        Addresses addresses = profile.getAddresses();
        if ( addresses == null )
        {
            addresses = Addresses.empty();
            profile.setAddresses( addresses );
        }
        profile.setAddresses( addresses.add( profileAddress ) );
    }

    @Autowired
    public void setDbAccountsLoader( final DatabaseAccountsLoader dbAccountsLoader )
    {
        this.dbAccountsLoader = dbAccountsLoader;
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Autowired
    public void setAccountSearchService( final AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }
}
