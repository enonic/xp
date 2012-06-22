package com.enonic.wem.core.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.UserInfoHelper;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.store.support.EntityPageList;

import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUP_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORES_ABSOLUTE_PATH;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USER_NODE_TYPE;
import static javax.jcr.query.qom.QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO;

@Component
public class AccountJcrDaoImpl
    extends JcrDaoSupport
        implements AccountJcrDao
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountJcrDaoImpl.class );

    @Override
    public UserEntity findUserByKey( final UserKey key )
    {
        UserEntity user = (UserEntity) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserByKey( session, key );
            }
        } );
        return user;
    }

    @Override
    public GroupEntity findGroupByKey( final GroupKey key )
    {
        GroupEntity group = (GroupEntity) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                    throws IOException, RepositoryException
            {
                return queryGroupByKey( session, key );
            }
        } );
        return group;

    }

    @Override
    public EntityPageList<UserEntity> findAll( final int index, final int count, final String query,
                                               final String order )
    {
        @SuppressWarnings("unchecked") EntityPageList<UserEntity> users =
            (EntityPageList<UserEntity>) getTemplate().execute( new JcrCallback()
            {
                public Object doInJcr( JcrSession session )
                    throws IOException, RepositoryException
                {
                    return queryAllUsers( session, index, count, query, order );
                }
            } );
        return users;
    }

    @Override
    public byte[] findUserPhotoByKey( final String key )
    {
        byte[] photo = (byte[]) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserPhotoByKey( session, new UserKey( key ) );
            }
        } );
        return photo;
    }

    private void setUserPhoto( final JcrSession session, final UserKey userKey, final UserEntity user )
            throws RepositoryException, IOException
    {
        final byte[] photo = queryUserPhotoByKey( session,userKey);
        user.setPhoto( photo );
    }

    private EntityPageList<UserEntity> queryAllUsers( JcrSession session, int index, int count, String query, String order )
        throws RepositoryException
    {
        QueryManager queryManager = session.getRealSession().getWorkspace().getQueryManager();
        QueryObjectModelFactory factory = queryManager.getQOMFactory();

        Selector source = factory.selector( USER_NODE_TYPE, "userNodes" );
        Column[] columns = null;
        Constraint constraint = factory.descendantNode( "userNodes", USERSTORES_ABSOLUTE_PATH );
        Ordering[] orderings = null;

        QueryObjectModel queryObj = factory.createQuery( source, constraint, orderings, columns );
        queryObj.setOffset( index );
        queryObj.setLimit( index );
        QueryResult result = queryObj.execute();

        NodeIterator nodeIterator = result.getNodes();

        LOG.info( nodeIterator.getSize() + " users found" );

        List<UserEntity> userList = new ArrayList<UserEntity>();
        while ( nodeIterator.hasNext() )
        {
            UserEntity user = new UserEntity();
            final JcrNode userNode = session.getNode( nodeIterator.nextNode() );
            nodePropertiesToUserFields( userNode, user );

            userList.add( user );

            LOG.info( user.toString() );
        }

        return new EntityPageList<UserEntity>( index, (int) nodeIterator.getSize(), userList );
    }

    private UserEntity queryUserByKey( JcrSession session, UserKey key )
            throws RepositoryException, IOException
    {
        QueryManager queryManager = session.getRealSession().getWorkspace().getQueryManager();
        QueryObjectModelFactory factory = queryManager.getQOMFactory();
        ValueFactory vf = session.getRealSession().getValueFactory();

        Selector source = factory.selector( USER_NODE_TYPE, "userNodes" );
        Column[] columns = null;
        Constraint constrUserstoresDescendant = factory.descendantNode( "userNodes", USERSTORES_ABSOLUTE_PATH );

        Constraint constrUserKey = factory.comparison( factory.propertyValue( "userNodes", "key" ), JCR_OPERATOR_EQUAL_TO,
                                                       factory.literal( vf.createValue( key.toString() ) ) );

        Constraint constraint = factory.and( constrUserstoresDescendant, constrUserKey );

        Ordering[] orderings = null;
        QueryObjectModel queryObj = factory.createQuery( source, constraint, orderings, columns );
        QueryResult result = queryObj.execute();

        NodeIterator nodeIterator = result.getNodes();

        final UserEntity user;
        if ( nodeIterator.hasNext() )
        {
            user = new UserEntity();
            final JcrNode userNode = session.getNode( nodeIterator.nextNode() );
            nodePropertiesToUserFields( userNode, user );
            setUserPhoto( session, key, user );
            setUserMemberships( session, userNode, user );
        }
        else
        {
            user = null;
        }
        return user;
    }

    private void setUserMemberships( JcrSession session, JcrNode userNode, UserEntity user )
    {
        final JcrPropertyIterator refIterator = userNode.getReferences();
        while ( refIterator.hasNext() )
        {
            final JcrProperty property = refIterator.next();
            final JcrNode memberOwnerNode = property.getNode();
            LOG.info( memberOwnerNode.getPath() );
        }
    }

    private GroupEntity queryGroupByKey( JcrSession session, GroupKey key )
            throws RepositoryException, IOException
    {
        QueryManager queryManager = session.getRealSession().getWorkspace().getQueryManager();
        QueryObjectModelFactory factory = queryManager.getQOMFactory();
        ValueFactory vf = session.getRealSession().getValueFactory();

        Selector source = factory.selector( GROUP_NODE_TYPE, "groupNodes" );
        Column[] columns = null;
        Constraint constrUserstoresDescendant = factory.descendantNode( "groupNodes", USERSTORES_ABSOLUTE_PATH );

        Constraint constrGroupKey =
                factory.comparison( factory.propertyValue( "groupNodes", "key" ), JCR_OPERATOR_EQUAL_TO,
                                    factory.literal( vf.createValue( key.toString() ) ) );

        Constraint constraint = factory.and( constrUserstoresDescendant, constrGroupKey );

        Ordering[] orderings = null;
        QueryObjectModel queryObj = factory.createQuery( source, constraint, orderings, columns );
        QueryResult result = queryObj.execute();

        NodeIterator nodeIterator = result.getNodes();

        LOG.info( nodeIterator.getSize() + " groups found" );

        GroupEntity group = null;
        if ( nodeIterator.hasNext() )
        {
            group = new GroupEntity();
            final JcrNode groupNode = session.getNode( nodeIterator.nextNode() );
            nodePropertiesToGroupFields( groupNode, group );
        }

        return group;
    }

    private byte[] queryUserPhotoByKey( JcrSession session, UserKey key )
        throws RepositoryException, IOException
    {
        QueryManager queryManager = session.getRealSession().getWorkspace().getQueryManager();
        QueryObjectModelFactory factory = queryManager.getQOMFactory();
        ValueFactory vf = session.getRealSession().getValueFactory();

        Selector source = factory.selector( USER_NODE_TYPE, "userNodes" );
        Column[] columns = null;
        Constraint constrUserstoresDescendant = factory.descendantNode( "userNodes", USERSTORES_ABSOLUTE_PATH );

        Constraint constrUserKey = factory.comparison( factory.propertyValue( "userNodes", "key" ), JCR_OPERATOR_EQUAL_TO,
                                                       factory.literal( vf.createValue( key.toString() ) ) );

        Constraint constraint = factory.and( constrUserstoresDescendant, constrUserKey );

        Ordering[] orderings = null;
        QueryObjectModel queryObj = factory.createQuery( source, constraint, orderings, columns );
        QueryResult result = queryObj.execute();

        NodeIterator nodeIterator = result.getNodes();

        byte[] photo = new byte[0];
        if ( nodeIterator.hasNext() )
        {
            Node userNode = nodeIterator.nextNode();
            if ( userNode.hasProperty( "photo" ) )
            {
                Binary binaryPhoto = userNode.getProperty( "photo" ).getValue().getBinary();
                photo = IOUtils.toByteArray( binaryPhoto.getStream() );
            }
        }

        return photo;
    }

    private void nodePropertiesToUserFields( JcrNode userNode, UserEntity user )
        throws RepositoryException
    {
        user.setName( userNode.getName() );
        user.setDisplayName( userNode.getProperty( "displayname" ).getString() );
        if ( userNode.hasProperty( "email" ) )
        {
            user.setEmail( userNode.getProperty( "email" ).getString() );
        }
        user.setKey( new UserKey( userNode.getProperty( "key" ).getString() ) );

        Calendar lastmodified = userNode.getProperty( "lastModified" ).getDate();
        if ( lastmodified != null )
        {
            DateTime timestamp = new DateTime( lastmodified );
            user.setTimestamp( timestamp );
        }

        user.setType( userTypeFromString( userNode.getProperty( "userType" ).getString() ) );

        UserStoreEntity userstore = new UserStoreEntity();
        nodeToUserstore( userNode.getParent().getParent(), userstore );
        user.setUserStore( userstore );

        final UserInfo userInfo = nodePropertiesToUserFields( userNode );
        final Address[] addresses = nodePropertiesToAddresses( userNode );
        userInfo.setAddresses( addresses );
        final UserFields userFields = UserInfoHelper.toUserFields( userInfo );
        user.setUserFields( userFields );
    }

    private Address[] nodePropertiesToAddresses( JcrNode userNode )
    {
        final List<Address> addressList = new ArrayList<Address>();
        final JcrNode addresses = userNode.getNode( "addresses" );
        JcrNodeIterator addressNodeIt = addresses.getNodes( "address" );
        while ( addressNodeIt.hasNext() )
        {
            JcrNode addressNode = addressNodeIt.next();
            final Address address = new Address();
            address.setLabel( addressNode.getPropertyString( "label" ) );
            address.setStreet( addressNode.getPropertyString( "street" ) );
            address.setPostalAddress( addressNode.getPropertyString( "postalAddress" ) );
            address.setPostalCode( addressNode.getPropertyString( "postalCode" ) );
            address.setRegion( addressNode.getPropertyString( "region" ) );
            address.setCountry( addressNode.getPropertyString( "country" ) );
            address.setIsoRegion( addressNode.getPropertyString( "isoRegion" ) );
            address.setIsoCountry( addressNode.getPropertyString( "isoCountry" ) );
            addressList.add( address );
        }

        return addressList.toArray( new Address[addressList.size()] );
    }

    private UserInfo nodePropertiesToUserFields( final JcrNode userNode )
            throws RepositoryException
    {
        final UserInfo info = new UserInfo();

        info.setBirthday( userNode.getPropertyDate( "birthday" ) );
        info.setCountry( userNode.getPropertyString( "country" ) );
        info.setDescription( userNode.getPropertyString( "description" ) );
        info.setFax( userNode.getPropertyString( "fax" ) );
        info.setFirstName( userNode.getPropertyString( "firstname" ) );
        info.setGlobalPosition( userNode.getPropertyString( "globalposition" ) );
        info.setHomePage( userNode.getPropertyString( "homepage" ) );
        info.setHtmlEmail( userNode.getPropertyBoolean( "htmlemail" ) );
        info.setInitials( userNode.getPropertyString( "initials" ) );
        info.setLastName( userNode.getPropertyString( "lastname" ) );
        final String locale = userNode.getPropertyString( "locale" );
        if ( locale != null )
        {
            info.setLocale( new Locale( locale ) );
        }
        info.setMemberId( userNode.getPropertyString( "memberid" ) );
        info.setMiddleName( userNode.getPropertyString( "middlename" ) );
        info.setMobile( userNode.getPropertyString( "mobile" ) );
        info.setOrganization( userNode.getPropertyString( "organization" ) );
        info.setPersonalId( userNode.getPropertyString( "personalid" ) );
        info.setPhone( userNode.getPropertyString( "phone" ) );
        info.setPrefix( userNode.getPropertyString( "prefix" ) );
        info.setSuffix( userNode.getPropertyString( "suffix" ) );
        final String timezone = userNode.getPropertyString( "timezone" );
        if ( timezone != null )
        {
            info.setTimezone( TimeZone.getTimeZone( timezone ) );
        }
        info.setTitle( userNode.getPropertyString( "title" ) );
        final String gender = userNode.getPropertyString( "gender" );
        if ( gender != null )
        {
            info.setGender( Gender.valueOf( gender ) );
        }
        info.setOrganization( userNode.getPropertyString( "organization" ) );

        return info;
    }

    private void nodePropertiesToGroupFields( JcrNode groupNode, GroupEntity group )
            throws RepositoryException
    {
        group.setName( groupNode.getName() );
        if ( groupNode.hasProperty( "description" ) )
        {
            group.setDescription( groupNode.getProperty( "description" ).getString() );
        }
        group.setKey( new GroupKey( groupNode.getProperty( "key" ).getString() ) );
        group.setType( groupTypeFromString( (int) groupNode.getProperty( "groupType" ).getLong() ) );
        group.setRestricted( true );

        UserStoreEntity userstore = new UserStoreEntity();
        nodeToUserstore( groupNode.getParent().getParent(), userstore );
        group.setUserStore( userstore );
    }

    private void nodeToUserstore( JcrNode userStoreNode, UserStoreEntity userStoreEntity )
        throws RepositoryException
    {
        userStoreEntity.setName( userStoreNode.getName() );
        userStoreEntity.setKey( new UserStoreKey( userStoreNode.getPropertyString( "key" ) ) );
    }

    private GroupType groupTypeFromString(final int value)
    {
        return GroupType.get( value );
    }

    private UserType userTypeFromString(final String value)
    {
        return UserType.valueOf( value.toUpperCase() );
    }
}
