package com.enonic.wem.web.rest.service.account;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchResults;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
public class AccountCsvExportService
{
    private static final DateTimeFormatter csvTimestampFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm ZZ" );

    private static final DateTimeFormatter csvDateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd" );

    private static final DateTimeFormatter filenameFormatter = DateTimeFormat.forPattern( "yyyyMMdd'T'HHmmss" );

    static final String GLOBAL_USERSTORE = "System";

    static final String ACCOUNT_TYPE_GROUP = "Group";

    static final String ACCOUNT_TYPE_ROLE = "Role";

    static final String ACCOUNT_TYPE_USER = "User";

    static final String DEFAULT_SEPARATOR = ",";


    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    public String getExportFileName( Date timestamp )
    {
        final String dateFormatted = filenameFormatter.print( new DateTime( timestamp ) );
        return String.format( "Accounts-%s.csv", dateFormatted );
    }

    public String generateCsv( AccountSearchResults accounts )
    {
        return generateCsv( accounts, DEFAULT_SEPARATOR );
    }

    public String generateCsv( AccountSearchResults accounts, String separator )
    {
        final CsvBuilder csvBuilder = new CsvBuilder().setSeparator( separator );
        addCsvHeader( csvBuilder );

        for ( AccountSearchHit accountHit : accounts )
        {
            switch ( accountHit.getAccountType() )
            {
                case ROLE:
                case GROUP:
                    final GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( accountHit.getKey().toString() ) );
                    if ( groupEntity != null )
                    {
                        addGroupToCsv( csvBuilder, groupEntity );
                    }
                    break;

                case USER:
                    final UserEntity userEntity = this.userDao.findByKey( accountHit.getKey().toString() );
                    if ( userEntity != null )
                    {
                        addUserToCsv( csvBuilder, userEntity );
                    }
                    break;
            }
            csvBuilder.endOfLine();
        }

        return csvBuilder.build();
    }

    private void addCsvHeader( CsvBuilder csvBuilder )
    {
        csvBuilder.addValue( "Type" );
        csvBuilder.addValue( "Display Name" );
        csvBuilder.addValue( "Local Name" );
        csvBuilder.addValue( "User Store" );
        csvBuilder.addValue( "Last Modified" );
        csvBuilder.addValue( "Description" );
        csvBuilder.addValue( "Email" );
        csvBuilder.addValue( "First Name" );
        csvBuilder.addValue( "Middle Name" );
        csvBuilder.addValue( "Last Name" );
        csvBuilder.addValue( "Initials" );
        csvBuilder.addValue( "Title" );
        csvBuilder.addValue( "Prefix" );
        csvBuilder.addValue( "Suffix" );
        csvBuilder.addValue( "Nickname" );
        csvBuilder.addValue( "Gender" );

        csvBuilder.addValue( "Birthdate" );
        csvBuilder.addValue( "Organization" );
        csvBuilder.addValue( "Country" );
        csvBuilder.addValue( "Global Position" );
        csvBuilder.addValue( "Home Page" );
        csvBuilder.addValue( "Locale" );
        csvBuilder.addValue( "Member Id" );
        csvBuilder.addValue( "Personal Id" );
        csvBuilder.addValue( "Phone" );
        csvBuilder.addValue( "Mobile" );
        csvBuilder.addValue( "Fax" );
        csvBuilder.addValue( "Time Zone" );

        csvBuilder.addValue( "Address Label" );
        csvBuilder.addValue( "Address Street" );
        csvBuilder.addValue( "Address Postal Address" );
        csvBuilder.addValue( "Address Postal Code" );
        csvBuilder.addValue( "Address Region" );
        csvBuilder.addValue( "Address ISO Region" );
        csvBuilder.addValue( "Address Country" );
        csvBuilder.addValue( "Address ISO Country" );

        csvBuilder.endOfLine();
    }

    private void addUserToCsv( CsvBuilder csvBuilder, UserEntity user )
    {
        csvBuilder.addValue( ACCOUNT_TYPE_USER );
        csvBuilder.addValue( user.getDisplayName() );
        csvBuilder.addValue( user.getQualifiedName().getUsername() );
        if ( user.getUserStore() != null )
        {
            csvBuilder.addValue( user.getUserStore().getName() );
        }
        else
        {
            csvBuilder.addValue( GLOBAL_USERSTORE );
        }

        // TODO: LastModified is not on UserEntity. Using timestamp instead.
        // final Date lastModif = user.getLastModified();
        final Date lastModif = user.getTimestamp().toDate();

        final String lastModifStr = ( lastModif == null ) ? "" : csvTimestampFormatter.print( new DateTime( lastModif ) );
        csvBuilder.addValue( lastModifStr );

        final UserInfo userInfo = UserInfoHelper.toUserInfo( user );
        csvBuilder.addValue( userInfo.getDescription() );
        csvBuilder.addValue( user.getEmail() );

        csvBuilder.addValue( userInfo.getFirstName() );
        csvBuilder.addValue( userInfo.getMiddleName() );
        csvBuilder.addValue( userInfo.getLastName() );
        csvBuilder.addValue( userInfo.getInitials() );
        csvBuilder.addValue( userInfo.getTitle() );
        csvBuilder.addValue( userInfo.getPrefix() );
        csvBuilder.addValue( userInfo.getSuffix() );
        csvBuilder.addValue( userInfo.getNickName() );

        final String gender = ( userInfo.getGender() == null ) ? "" : userInfo.getGender().toString();
        csvBuilder.addValue( gender );

        final Date birthday = userInfo.getBirthday();
        final String birthdayStr = ( birthday == null ) ? "" : csvDateFormatter.print( new DateTime( birthday ) );
        csvBuilder.addValue( birthdayStr );
        csvBuilder.addValue( userInfo.getOrganization() );
        csvBuilder.addValue( userInfo.getCountry() );
        csvBuilder.addValue( userInfo.getGlobalPosition() );
        csvBuilder.addValue( userInfo.getHomePage() );
        final String localeStr = ( userInfo.getLocale() == null ) ? "" : userInfo.getLocale().toString();
        csvBuilder.addValue( localeStr );
        csvBuilder.addValue( userInfo.getMemberId() );
        csvBuilder.addValue( userInfo.getPersonalId() );
        csvBuilder.addValue( userInfo.getPhone() );
        csvBuilder.addValue( userInfo.getMobile() );
        csvBuilder.addValue( userInfo.getFax() );
        final String timeZoneStr = ( userInfo.getTimeZone() == null ) ? "" : userInfo.getTimeZone().getDisplayName();
        csvBuilder.addValue( timeZoneStr );

        final Address address = userInfo.getPrimaryAddress();
        if ( address != null )
        {
            csvBuilder.addValue( address.getLabel() );
            csvBuilder.addValue( address.getStreet() );
            csvBuilder.addValue( address.getPostalAddress() );
            csvBuilder.addValue( address.getPostalCode() );
            csvBuilder.addValue( address.getRegion() );
            csvBuilder.addValue( address.getIsoRegion() );
            csvBuilder.addValue( address.getCountry() );
            csvBuilder.addValue( address.getIsoCountry() );
        }

    }

    private void addGroupToCsv( CsvBuilder csvBuilder, GroupEntity group )
    {
        csvBuilder.addValue( group.isBuiltIn() ? ACCOUNT_TYPE_ROLE : ACCOUNT_TYPE_GROUP );

        // TODO: DisplayName is not on GroupEntity. Using description instead.
        // csvBuilder.addValue( group.getDisplayName() );
        csvBuilder.addValue( group.getDescription() );

        csvBuilder.addValue( group.getQualifiedName().getGroupname() );
        if ( group.getUserStore() != null )
        {
            csvBuilder.addValue( group.getUserStore().getName() );
        }
        else
        {
            csvBuilder.addValue( GLOBAL_USERSTORE );
        }

        // TODO: LastModified is not on GroupEntity. Using "null" instead.
        // final Date lastModif = group.getLastModified();
        final Date lastModif = null;

        final String lastModifStr = ( lastModif == null ) ? "" : csvTimestampFormatter.print( new DateTime( lastModif ) );
        csvBuilder.addValue( lastModifStr );
        csvBuilder.addValue( group.getDescription() );
    }


    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

}


