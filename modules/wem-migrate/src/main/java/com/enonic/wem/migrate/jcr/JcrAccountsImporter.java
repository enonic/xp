package com.enonic.wem.migrate.jcr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.JcrCallback;
import com.enonic.wem.core.jcr.JcrCmsConstants;
import com.enonic.wem.core.jcr.JcrDaoSupport;
import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrSession;

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

import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUPS_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUP_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORES_PATH;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERS_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USER_NODE_TYPE;

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

    @Autowired
    private JdbcAccountsRetriever jdbcAccountsRetriever;

    private final Map<Integer, String> userStoreKeyName;

    private final Map<String, String> groupKeyUIDMapping;

    public JcrAccountsImporter()
    {
        userStoreKeyName = new HashMap<Integer, String>();
        groupKeyUIDMapping = new HashMap<String, String>();
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
                storeUserstore( userstoreFields );
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
                    final String accountKey = (String) data.get( "GGM_MBR_GRP_HKEY" );
                    final String memberKey = (String) data.get( "GGM_GRP_HKEY" );
                }
                catch ( Exception e )
                {
                    LOG.error( "Unable to import membership: " + data.get( "GGM_MBR_GRP_HKEY" ), e );
                }
            }
        } );
    }

    private void releaseResources()
    {
        userStoreKeyName.clear();
        groupKeyUIDMapping.clear();
    }

    private void storeGroup( final Map<String, Object> groupFields )
    {
        getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                    throws IOException, RepositoryException
            {
                addGroup( session, groupFields );

                session.save();

                return null;
            }
        } );
    }

    private void storeUser( final Map<String, Object> userFields )
    {
        getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                addUser( session, userFields );

                session.save();

                return null;
            }
        } );
    }

    private void storeUserstore( final Map<String, Object> userstoreFields )
    {
        getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                addUserstore( session, userstoreFields );

                session.save();

                return null;
            }
        } );
    }

    private void addUser( JcrSession session, Map<String, Object> userFields )
        throws RepositoryException, UnsupportedEncodingException
    {
        String userName = (String) userFields.get( "USR_SUID" );
        Integer userStoreKey = (Integer) userFields.get( "USR_DOM_LKEY" );
        String userstoreNodeName = userStoreKeyName.get( userStoreKey );
        if ( userstoreNodeName == null )
        {
            userStoreKey = SYSTEM_USERSTORE_KEY;
            userstoreNodeName = userStoreKeyName.get( userStoreKey );
        }

        String userParentNodePath = USERSTORES_PATH + userstoreNodeName + "/" + USERS_NODE;
        JcrNode userNode = session.getRootNode().getNode( userParentNodePath ).addNode( userName, USER_NODE_TYPE );

        // common user properties
        String qualifiedName = (String) userFields.get( "USR_SUID" );
        String displayName = (String) userFields.get( "USR_SFULLNAME" );
        String email = (String) userFields.get( "USR_SEMAIL" );
        String key = (String) userFields.get( "USR_HKEY" );
        Date lastModified = (Date) userFields.get( "USR_DTETIMESTAMP" );
        String syncValue = (String) userFields.get( "USR_SSYNCVALUE" );
        byte[] photo = (byte[]) userFields.get( "USR_PHOTO" );
        UserType userType = UserType.getByKey( (Integer) userFields.get( "USR_UT_LKEY" ) );

        userNode.setPropertyString( "qualifiedName", qualifiedName );
        userNode.setPropertyString( "displayname", displayName );
        userNode.setPropertyString( "email", email );
        userNode.setPropertyString( "key", key );
        userNode.setPropertyDate( "lastModified", lastModified );
        userNode.setPropertyString( "syncValue", syncValue );
        userNode.setPropertyString( "userType", userType.getName() );
        if ( photo != null )
        {
            userNode.setPropertyBinary( "photo", photo );
        }

        // user info fields
        Map<String, Object> userInfoFields = (Map<String, Object>) userFields.get( JdbcAccountsRetriever.USER_INFO_FIELDS_MAP );
        addUserInfoFields( userNode, userInfoFields );

        LOG.info( "User imported: " + userName );
    }

    private void addUserInfoFields( JcrNode userNode, Map<String, Object> userInfoFields )
            throws RepositoryException
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

        final Address[] addresses  = userFieldsToAddresses(userInfoFields);
        userInfo.setAddresses( addresses );

        userInfoFieldsToNode( userInfo, userNode );
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

    private void addGroup( JcrSession session, Map<String, Object> groupFields )
            throws RepositoryException
    {
        String groupName = (String) groupFields.get( "GRP_SNAME" );
        GroupType groupType = GroupType.get( (Integer) groupFields.get( "GRP_LTYPE" ) );
        if ( groupType == GroupType.USER )
        {
            LOG.debug( "Skipping group of type User: " + groupName );
            return;
        }
        Integer userStoreKey = (Integer) groupFields.get( "GRP_DOM_LKEY" );
        String userstoreNodeName = userStoreKeyName.get( userStoreKey );
        if ( userstoreNodeName == null )
        {
            userStoreKey = SYSTEM_USERSTORE_KEY;
            userstoreNodeName = userStoreKeyName.get( userStoreKey );
        }

        String userParentNodePath = USERSTORES_PATH + userstoreNodeName + "/" + GROUPS_NODE;
        JcrNode userNode = session.getRootNode().getNode( userParentNodePath ).addNode( groupName, GROUP_NODE_TYPE );

        // common user properties
        String description = (String) groupFields.get( "GRP_SDESCRIPTION" );
        String key = (String) groupFields.get( "GRP_HKEY" );
        Date lastModified = new Date();
        String syncValue = (String) groupFields.get( "GRP_SSYNCVALUE" );

        userNode.setPropertyString( "qualifiedName", groupName );
        userNode.setPropertyString( "displayname", groupName );
        userNode.setPropertyString( "description", description );
        userNode.setPropertyString( "key", key );
        userNode.setPropertyDate( "lastModified", lastModified );
        userNode.setPropertyString( "syncValue", syncValue );
        userNode.setPropertyLong( "groupType", groupType.toInteger() );

        LOG.info( "Group imported: " + groupName );
    }

    private void addUserstore( JcrSession session, Map<String, Object> userstoreFields )
        throws RepositoryException, UnsupportedEncodingException
    {
        String userstoreName = (String) userstoreFields.get( "DOM_SNAME" );

        JcrNode userstoresNode = session.getRootNode().getNode( JcrCmsConstants.USERSTORES_PATH );
        if ( ( userstoreName == null ) || userstoresNode.hasNode( userstoreName ) )
        {
            LOG.info( "Skipping creation of existing user store: " + userstoreName );
            return;
        }

        Integer key = (Integer) userstoreFields.get( "DOM_LKEY" );
        boolean defaultUserstore = ( (Integer) userstoreFields.get( "DOM_BDEFAULTSTORE" ) == 1 );
        String connectorName = (String) userstoreFields.get( "DOM_SCONFIGNAME" );
        byte[] xmlBytes = (byte[]) userstoreFields.get( "DOM_XMLDATA" );
        String userStoreXmlConfig = new String( xmlBytes, "UTF-8" );

        JcrNode userstoreNode = userstoresNode.addNode( userstoreName, JcrCmsConstants.USERSTORE_NODE_TYPE );
        userstoreNode.setPropertyString( "key", key.toString() );
        userstoreNode.setPropertyBoolean( "default", defaultUserstore );
        userstoreNode.setPropertyString( "connector", connectorName );
        userstoreNode.setPropertyString( "xmlconfig", userStoreXmlConfig );

        userstoreNode.addNode( JcrCmsConstants.GROUPS_NODE, JcrCmsConstants.GROUPS_NODE_TYPE );
        userstoreNode.addNode( JcrCmsConstants.USERS_NODE, JcrCmsConstants.USERS_NODE_TYPE );

        userStoreKeyName.put( key, userstoreName );

        LOG.info( "Userstore imported: " + userstoreName );
    }

    private void userInfoFieldsToNode( UserInfo userInfo, JcrNode userNode )
        throws RepositoryException
    {
        userNode.setPropertyDate( "birthday", userInfo.getBirthday() );
        userNode.setPropertyString( "country", userInfo.getCountry() );
        userNode.setPropertyString( "description", userInfo.getDescription() );
        userNode.setPropertyString( "fax", userInfo.getFax() );
        userNode.setPropertyString( "firstname", userInfo.getFirstName() );
        userNode.setPropertyString( "globalposition", userInfo.getGlobalPosition() );
        userNode.setPropertyString( "homepage", userInfo.getHomePage() );
        Boolean htmlEmail = userInfo.getHtmlEmail();
        if ( htmlEmail != null )
        {
            userNode.setPropertyBoolean( "htmlemail", htmlEmail );
        }
        userNode.setPropertyString( "initials", userInfo.getInitials() );
        userNode.setPropertyString( "lastname", userInfo.getLastName() );
        Locale locale = userInfo.getLocale();
        if ( locale != null )
        {
            userNode.setPropertyString( "locale", locale.getISO3Language() );
        }
        userNode.setPropertyString( "memberid", userInfo.getMemberId() );
        userNode.setPropertyString( "middlename", userInfo.getMiddleName() );
        userNode.setPropertyString( "mobile", userInfo.getMobile() );
        userNode.setPropertyString( "organization", userInfo.getOrganization() );
        userNode.setPropertyString( "personalid", userInfo.getPersonalId() );
        userNode.setPropertyString( "phone", userInfo.getPhone() );
        userNode.setPropertyString( "prefix", userInfo.getPrefix() );
        userNode.setPropertyString( "suffix", userInfo.getSuffix() );
        TimeZone timezone = userInfo.getTimeZone();
        if ( timezone != null )
        {
            userNode.setPropertyString( "timezone", timezone.getID() );
        }
        userNode.setPropertyString( "title", userInfo.getTitle() );
        Gender gender = userInfo.getGender();
        if ( gender != null )
        {
            userNode.setPropertyString( "gender", gender.toString() );
        }
        userNode.setPropertyString( "organization", userInfo.getOrganization() );

        final Address[] addresses = userInfo.getAddresses();
        final JcrNode addressesNode = userNode.addNode( "addresses" );
        for ( Address address : addresses )
        {
            addAddressNode( address, addressesNode );
        }
    }

    private void addAddressNode( Address address, JcrNode addressesNode )
            throws RepositoryException
    {
        final JcrNode addressNode = addressesNode.addNode( "address" );
        addressNode.setPropertyString( "country", address.getCountry() );
        addressNode.setPropertyString( "isoCountry", address.getIsoCountry() );
        addressNode.setPropertyString( "isoRegion", address.getIsoRegion() );
        addressNode.setPropertyString( "label", address.getLabel() );
        addressNode.setPropertyString( "postalAddress", address.getPostalAddress() );
        addressNode.setPropertyString( "postalCode", address.getPostalCode() );
        addressNode.setPropertyString( "region", address.getRegion() );
        addressNode.setPropertyString( "street", address.getStreet() );
    }
}
