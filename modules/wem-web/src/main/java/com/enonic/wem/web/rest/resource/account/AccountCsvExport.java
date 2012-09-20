package com.enonic.wem.web.rest.resource.account;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.web.rest.service.account.CsvBuilder;

final class AccountCsvExport
{
    private static final DateTimeFormatter csvTimestampFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm ZZ" );

    private static final DateTimeFormatter csvDateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd" );

    private static final String ACCOUNT_TYPE_GROUP = "Group";

    private static final String ACCOUNT_TYPE_ROLE = "Role";

    private static final String ACCOUNT_TYPE_USER = "User";

    private static final String[] HEADER_TEXTS =
        {"Type", "Display Name", "Local Name", "User Store", "Last Modified", "Email", "First Name", "Middle Name", "Last Name", "Initials",
            "Title", "Prefix", "Suffix", "Nickname", "Gender", "Birthdate", "Organization", "Country", "Global Position", "Home Page",
            "Locale", "Member Id", "Personal Id", "Phone", "Mobile", "Fax", "Time Zone", "Address Label", "Address Street",
            "Address Postal Address", "Address Postal Code", "Address Region", "Address ISO Region", "Address Country",
            "Address ISO Country"};


    public String generateCsvContent( Accounts accounts, String separator )
    {
        final CsvBuilder csvBuilder = new CsvBuilder().setSeparator( separator );
        addCsvHeader( csvBuilder );

        for ( Account account : accounts )
        {
            switch ( account.getKey().getType() )
            {
                case ROLE:
                case GROUP:
                    addGroupToCsv( csvBuilder, (NonUserAccount) account );
                    break;

                case USER:
                    addUserToCsv( csvBuilder, (UserAccount) account );
                    break;
            }
            csvBuilder.endOfLine();
        }

        return csvBuilder.build();
    }

    private void addCsvHeader( CsvBuilder csvBuilder )
    {
        for ( String headerText : HEADER_TEXTS )
        {
            csvBuilder.addValue( headerText );
        }
        csvBuilder.endOfLine();
    }

    private void addUserToCsv( CsvBuilder csvBuilder, UserAccount user )
    {
        csvBuilder.addValue( ACCOUNT_TYPE_USER );
        csvBuilder.addValue( user.getDisplayName() );
        csvBuilder.addValue( user.getKey().getLocalName() );
        csvBuilder.addValue( user.getKey().getUserStore() );
        final DateTime lastModified = user.getModifiedTime();
        csvBuilder.addValue( ( lastModified == null ) ? "" : csvTimestampFormatter.print( lastModified ) );

        csvBuilder.addValue( user.getEmail() );
        final UserProfile profile = user.getProfile();
        if ( profile != null )
        {
            addUserProfileToCsv( csvBuilder, profile );
        }
    }

    private void addUserProfileToCsv( CsvBuilder csvBuilder, UserProfile profile )
    {
        csvBuilder.addValue( profile.getFirstName() );
        csvBuilder.addValue( profile.getMiddleName() );
        csvBuilder.addValue( profile.getLastName() );
        csvBuilder.addValue( profile.getInitials() );
        csvBuilder.addValue( profile.getTitle() );
        csvBuilder.addValue( profile.getPrefix() );
        csvBuilder.addValue( profile.getSuffix() );
        csvBuilder.addValue( profile.getNickName() );

        final String gender = ( profile.getGender() == null ) ? "" : profile.getGender().toString();
        csvBuilder.addValue( gender );

        final DateTime birthday = profile.getBirthday();
        final String birthdayStr = ( birthday == null ) ? "" : csvDateFormatter.print( birthday );
        csvBuilder.addValue( birthdayStr );
        csvBuilder.addValue( profile.getOrganization() );
        csvBuilder.addValue( profile.getCountry() );
        csvBuilder.addValue( profile.getGlobalPosition() );
        csvBuilder.addValue( profile.getHomePage() );
        final String localeStr = ( profile.getLocale() == null ) ? "" : profile.getLocale().toString();
        csvBuilder.addValue( localeStr );
        csvBuilder.addValue( profile.getMemberId() );
        csvBuilder.addValue( profile.getPersonalId() );
        csvBuilder.addValue( profile.getPhone() );
        csvBuilder.addValue( profile.getMobile() );
        csvBuilder.addValue( profile.getFax() );
        final String timeZoneStr = ( profile.getTimeZone() == null ) ? "" : profile.getTimeZone().getDisplayName();
        csvBuilder.addValue( timeZoneStr );

        final Addresses addresses = profile.getAddresses();
        final Address address = addresses != null ? addresses.getPrimary() : null;
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

    private void addGroupToCsv( CsvBuilder csvBuilder, NonUserAccount nonUserAccount )
    {
        csvBuilder.addValue( nonUserAccount.getKey().isRole() ? ACCOUNT_TYPE_ROLE : ACCOUNT_TYPE_GROUP );
        csvBuilder.addValue( nonUserAccount.getDisplayName() );
        csvBuilder.addValue( nonUserAccount.getKey().getLocalName() );
        csvBuilder.addValue( nonUserAccount.getKey().getUserStore() );
        final DateTime lastModified = nonUserAccount.getModifiedTime();
        csvBuilder.addValue( ( lastModified == null ) ? "" : csvTimestampFormatter.print( lastModified ) );
    }
}