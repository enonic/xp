package com.enonic.wem.migrate.jcr;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.old.JcrDaoSupport;
import com.enonic.wem.core.jcr.old.accounts.AccountJcrDao;
import com.enonic.wem.core.jcr.old.accounts.JcrAddress;
import com.enonic.wem.core.jcr.old.accounts.JcrGroup;
import com.enonic.wem.core.jcr.old.accounts.JcrRole;
import com.enonic.wem.core.jcr.old.accounts.JcrUser;
import com.enonic.wem.core.jcr.old.accounts.JcrUserInfo;
import com.enonic.wem.core.jcr.old.accounts.JcrUserStore;

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
        extends JcrDaoSupport
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrAccountsImporter.class );

    private final static String F_COUNTRY = "country";

    private final static String F_ISO_COUNTRY = "iso-country";

    private final static String F_REGION = "region";

    private final static String F_ISO_REGION = "iso-region";

    private final static String F_LABEL = "label";

    private final static String F_STREET = "street";

    private final static String F_POSTAL_CODE = "postal-code";

    private final static String F_POSTAL_ADDRESS = "postal-address";

    private static final int SYSTEM_USERSTORE_KEY = 0;

    private JdbcAccountsRetriever jdbcAccountsRetriever;

    private AccountJcrDao accountDao;

    private final Map<Integer, String> userStoreKeyName;

    private final Map<String, String> accountKeyToJcrUIDMapping;

    public JcrAccountsImporter()
    {
        userStoreKeyName = new HashMap<Integer, String>();
        accountKeyToJcrUIDMapping = new HashMap<String, String>();
    }

    public void importAccounts()
    {
        importUserStores();

        importUsers();

        importGroups();

        releaseResources();
    }

    private void importUserStores()
    {
        jdbcAccountsRetriever.fetchUserStores( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> userstoreFields )
            {
                try
                {
                    importUserStore( userstoreFields );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import userstore", e );
                }
            }
        } );
    }

    private void importUsers()
    {
        jdbcAccountsRetriever.fetchUsers( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                try
                {
                    storeUser( data );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import user: " + data.get( "USR_SUID" ), e );
                }
            }
        } );
    }

    private void importGroups()
    {
        jdbcAccountsRetriever.fetchGroups( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                try
                {
                    storeGroup( data );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import group: " + data.get( "GRP_SNAME" ), e );
                }
            }
        } );

        jdbcAccountsRetriever.fetchMemberships( new ImportDataCallbackHandler()
        {
            public void processDataEntry( Map<String, Object> data )
            {
                try
                {
                    final String memberKey = (String) data.get( "GGM_MBR_GRP_HKEY" );
                    final String accountKey = (String) data.get( "GGM_GRP_HKEY" );
                    addMembership( accountKey, memberKey );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import membership: " + data.get( "GGM_MBR_GRP_HKEY" ), e );
                }
            }
        } );
    }

    private void addMembership( final String groupKey, final String memberKey )
    {
        final String groupId = accountKeyToJcrUIDMapping.get( groupKey );
        final String memberId = accountKeyToJcrUIDMapping.get( memberKey );
        if ( ( memberId == null ) || ( groupId == null ) )
        {
            return;
        }
        accountDao.addMemberships( groupId, memberId );
        LOG.info( "Added account " + memberKey + " as member of " + groupKey );
    }

    private void releaseResources()
    {
        userStoreKeyName.clear();
        accountKeyToJcrUIDMapping.clear();
    }

    private void storeGroup( final Map<String, Object> groupFields )
    {
        final String groupName = (String) groupFields.get( "GRP_SNAME" );
        final GroupType groupType = GroupType.get( (Integer) groupFields.get( "GRP_LTYPE" ) );
        if ( groupType == GroupType.USER )
        {
            LOG.debug( "Skipping group of type User: " + groupName );
            return;
        }
        Integer userStoreKey = (Integer) groupFields.get( "GRP_DOM_LKEY" );
        String userstoreName = userStoreKeyName.get( userStoreKey );
        if ( userstoreName == null )
        {
            userStoreKey = SYSTEM_USERSTORE_KEY;
            userstoreName = userStoreKeyName.get( userStoreKey );
        }
        final JcrGroup group = groupType.isBuiltIn() ? new JcrRole() : new JcrGroup();
        final String description = (String) groupFields.get( "GRP_SDESCRIPTION" );
        final DateTime lastModified = new DateTime();
        final String syncValue = (String) groupFields.get( "GRP_SSYNCVALUE" );

        group.setName( groupName );
        group.setDisplayName( groupName );
        group.setDescription( description );
        group.setLastModified(lastModified );
        group.setSyncValue(  syncValue );
        group.setUserStore( userstoreName );

        accountDao.saveAccount( group );

        final String groupId = group.getId();
        final String groupKey = (String) groupFields.get( "GRP_HKEY" );
        accountKeyToJcrUIDMapping.put( groupKey, groupId );
        LOG.info( "Group '" + groupName + "' imported with id " + groupId );
    }

    private void storeUser( final Map<String, Object> userFields )
    {
        final JcrUser user = importUser( userFields );
        if ( user != null )
        {
            final String userId = user.getId();
            final String userKey = (String) userFields.get( "USR_GRP_HKEY" );
            accountKeyToJcrUIDMapping.put( userKey, userId );
            final String userName = (String) userFields.get( "USR_SUID" );
            LOG.info( "User '" + userName + "' imported with id " + userId );
        }
    }

    private void importUserStore( final Map<String, Object> userstoreFields )
        throws UnsupportedEncodingException
    {
        final String userstoreName = (String) userstoreFields.get( "DOM_SNAME" );
        final Integer key = (Integer) userstoreFields.get( "DOM_LKEY" );
        final boolean defaultUserstore = ( (Integer) userstoreFields.get( "DOM_BDEFAULTSTORE" ) == 1 );
        final String connectorName = (String) userstoreFields.get( "DOM_SCONFIGNAME" );
        final byte[] xmlBytes = (byte[]) userstoreFields.get( "DOM_XMLDATA" );
        final String userStoreXmlConfig = new String( xmlBytes, "UTF-8" );

        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( userstoreName );
        userStore.setId( key.toString() );
        userStore.setDefaultStore( defaultUserstore );
        userStore.setXmlConfig( userStoreXmlConfig );
        userStore.setConnectorName( connectorName );
        accountDao.createUserStore( userStore );

        userStoreKeyName.put( key, userstoreName );
        LOG.info( "Userstore imported: " + userstoreName );
    }

    private JcrUser importUser( Map<String, Object> userFields )
    {
        final String userName = (String) userFields.get( "USR_SUID" );
        Integer userStoreKey = (Integer) userFields.get( "USR_DOM_LKEY" );
        String userstoreNodeName = userStoreKeyName.get( userStoreKey );
        if ( userstoreNodeName == null )
        {
            userStoreKey = SYSTEM_USERSTORE_KEY;
            userstoreNodeName = userStoreKeyName.get( userStoreKey );
        }

        final String qualifiedName = (String) userFields.get( "USR_SUID" );
        final String displayName = (String) userFields.get( "USR_SFULLNAME" );
        final String email = (String) userFields.get( "USR_SEMAIL" );
        final String key = (String) userFields.get( "USR_HKEY" );
        final Date lastModified = (Date) userFields.get( "USR_DTETIMESTAMP" );
        final String syncValue = (String) userFields.get( "USR_SSYNCVALUE" );
        final byte[] photo = (byte[]) userFields.get( "USR_PHOTO" );
        final UserType userType = UserType.getByKey( (Integer) userFields.get( "USR_UT_LKEY" ) );

        final JcrUser user = new JcrUser();
        user.setName( userName );
        user.setDisplayName( displayName );
        user.setEmail( email );
        user.setLastModified( new DateTime( lastModified ) );
        user.setSyncValue( syncValue );
        user.setPhoto( photo );
        user.setBuiltIn( userType.isBuiltIn() );
        user.setUserStore( userstoreNodeName );

        // user info fields
        final Map<String, Object> userInfoFields = (Map<String, Object>) userFields.get( JdbcAccountsRetriever.USER_INFO_FIELDS_MAP );
        addUserInfoFields( user, userInfoFields );

        accountDao.saveAccount( user );
        return user;
    }

    private void addUserInfoFields( JcrUser user, Map<String, Object> userInfoFields )
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

        userInfoFieldsToNode( userInfo, user );
    }

    private Address[] userFieldsToAddresses( Map<String, Object> userFields )
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
                if ( F_LABEL.equals( fieldId ) )
                {
                    address.setLabel( value );
                }
                else if ( F_COUNTRY.equals( fieldId ) )
                {
                    address.setCountry( value );
                }
                else if ( F_ISO_COUNTRY.equals( fieldId ) )
                {
                    address.setIsoCountry( value );
                }
                else if ( F_REGION.equals( fieldId ) )
                {
                    address.setRegion( value );
                }
                else if ( F_ISO_REGION.equals( fieldId ) )
                {
                    address.setIsoRegion( value );
                }
                else if ( F_STREET.equals( fieldId ) )
                {
                    address.setStreet( value );
                }
                else if ( F_POSTAL_CODE.equals( fieldId ) )
                {
                    address.setPostalCode( value );
                }
                else if ( F_POSTAL_ADDRESS.equals( fieldId ) )
                {
                    address.setPostalAddress( value );
                }
            }
        }
        return addresses.values().toArray( new Address[addresses.size()] );
    }

    private void userInfoFieldsToNode( UserInfo userInfo, JcrUser user )
    {
        JcrUserInfo jcrUserInfo = user.getUserInfo();
        if ( userInfo.getBirthday() != null )
        {
            jcrUserInfo.setBirthday( new DateTime( userInfo.getBirthday() ) );
        }
        jcrUserInfo.setCountry( userInfo.getCountry() );
        jcrUserInfo.setDescription( userInfo.getDescription() );
        jcrUserInfo.setFax( userInfo.getFax() );
        jcrUserInfo.setFirstName( userInfo.getFirstName() );
        jcrUserInfo.setGlobalPosition( userInfo.getGlobalPosition() );
        jcrUserInfo.setHomePage( userInfo.getHomePage() );
        jcrUserInfo.setHtmlEmail( userInfo.getHtmlEmail() );
        jcrUserInfo.setInitials( userInfo.getInitials() );
        jcrUserInfo.setLastName( userInfo.getLastName() );
        Locale locale = userInfo.getLocale();
        if ( locale != null )
        {
            jcrUserInfo.setLocale( locale.getISO3Language() );
        }
        jcrUserInfo.setMemberId( userInfo.getMemberId() );
        jcrUserInfo.setMiddleName( userInfo.getMiddleName() );
        jcrUserInfo.setMobile( userInfo.getMobile() );
        jcrUserInfo.setOrganization( userInfo.getOrganization() );
        jcrUserInfo.setPersonalId( userInfo.getPersonalId() );
        jcrUserInfo.setPhone( userInfo.getPhone() );
        jcrUserInfo.setPrefix( userInfo.getPrefix() );
        jcrUserInfo.setSuffix( userInfo.getSuffix() );
        TimeZone timezone = userInfo.getTimeZone();
        if ( timezone != null )
        {
            jcrUserInfo.setTimeZone( timezone.getID() );
        }
        jcrUserInfo.setTitle( userInfo.getTitle() );
        Gender gender = userInfo.getGender();
        if ( gender != null )
        {
            jcrUserInfo.setGender( com.enonic.wem.core.jcr.old.accounts.Gender.fromName( gender.toString() ) );
        }
        jcrUserInfo.setOrganization( userInfo.getOrganization() );

        final Address[] addresses = userInfo.getAddresses();
        for ( Address address : addresses )
        {
            addAddressNode( address, jcrUserInfo );
        }
    }

    private void addAddressNode( Address address, JcrUserInfo jcrUserInfo )
    {
        final JcrAddress jcrAddress = new JcrAddress();
        jcrAddress.setCountry( address.getCountry() );
        jcrAddress.setIsoCountry( address.getIsoCountry() );
        jcrAddress.setIsoRegion( address.getIsoRegion() );
        jcrAddress.setLabel( address.getLabel() );
        jcrAddress.setPostalAddress( address.getPostalAddress() );
        jcrAddress.setPostalCode( address.getPostalCode() );
        jcrAddress.setRegion( address.getRegion() );
        jcrAddress.setStreet( address.getStreet() );

        jcrUserInfo.addAddress( jcrAddress );
    }

    @Autowired
    public void setJdbcAccountsRetriever( final JdbcAccountsRetriever jdbcAccountsRetriever )
    {
        this.jdbcAccountsRetriever = jdbcAccountsRetriever;
    }

    @Autowired
    public void setAccountDao( final AccountJcrDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
