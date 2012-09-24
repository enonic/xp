package com.enonic.wem.core.jcr.old.accounts;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;

import static org.junit.Assert.assertEquals;

public class AssertAccounts
{

    public static boolean usersEquals( JcrUser user1, JcrUser user2 )
    {
        if ( ( user1 == null ) || ( user2 == null ) )
        {
            return user1 == user2;
        }
        if ( !user1.equals( user2 ) )
        {
            return false;
        }
        final EqualsBuilder eb = new EqualsBuilder();
        eb.append( user1.getName(), user2.getName() );
        eb.append( user1.getSyncValue(), user2.getSyncValue() );
        eb.append( user1.getDisplayName(), user2.getDisplayName() );
        eb.append( user1.getEmail(), user2.getEmail() );
        eb.append( user1.getQualifiedName(), user2.getQualifiedName() );
        eb.append( user1.getUserStore(), user2.getUserStore() );
        eb.append( user1.getCreated(), user2.getCreated() );
        eb.append( user1.getPhoto(), user2.getPhoto() );
        eb.append( user1.getAccountType(), user2.getAccountType() );
        final boolean eq = eb.isEquals()
            && isDateTimeEquals( user1.getLastLogged(), user2.getLastLogged() )
            && isDateTimeEquals( user1.getLastModified(), user2.getLastModified() );

        return eq && usersInfoEquals( user1.getUserInfo(), user2.getUserInfo() );
    }

    public static boolean usersInfoEquals( JcrUserInfo user1, JcrUserInfo user2 )
    {
        if ( ( user1 == null ) || ( user2 == null ) )
        {
            return user1 == user2;
        }
        return EqualsBuilder.reflectionEquals( user1, user2 );
    }

    public static void assertUserEquals( JcrUser user1, JcrUser user2 )
    {
        assertUserEquals( null, user1, user2 );
    }

    public static void assertUserEquals( String message, JcrUser user1, JcrUser user2 )
    {
        boolean equals = usersEquals( user1, user2 );
        if ( !equals )
        {
            final String userStr1 = ToStringBuilder.reflectionToString( user1, ToStringStyle.MULTI_LINE_STYLE );
            final String userStr2 = ToStringBuilder.reflectionToString( user2, ToStringStyle.MULTI_LINE_STYLE );
            assertEquals( message, userStr1, userStr2 );
        }
    }

    public static boolean groupsEquals( JcrGroup group1, JcrGroup group2 )
    {
        if ( ( group1 == null ) || ( group2 == null ) )
        {
            return group1 == group2;
        }
        if ( !group1.equals( group2 ) )
        {
            return false;
        }
        final EqualsBuilder eb = new EqualsBuilder();
        eb.append( group1.getName(), group2.getName() );
        eb.append( group1.getSyncValue(), group2.getSyncValue() );
        eb.append( group1.getDisplayName(), group2.getDisplayName() );
        eb.append( group1.getLastModified(), group2.getLastModified() );
        eb.append( group1.getQualifiedName(), group2.getQualifiedName() );
        eb.append( group1.getUserStore(), group2.getUserStore() );
        eb.append( group1.getAccountType(), group2.getAccountType() );

        return eb.isEquals();
    }

    public static void assertGroupEquals( JcrGroup group1, JcrGroup group2 )
    {
        boolean equals = groupsEquals( group1, group2 );
        if ( !equals )
        {
            final String groupStr1 = ToStringBuilder.reflectionToString( group1, ToStringStyle.MULTI_LINE_STYLE );
            final String groupStr2 = ToStringBuilder.reflectionToString( group2, ToStringStyle.MULTI_LINE_STYLE );
            assertEquals( groupStr1, groupStr2 );
        }
    }

    private static boolean isDateTimeEquals( final DateTime dateTime1, final DateTime dateTime2 )
    {
        // dateTime1.equals(dateTime2) can give an unexpected result if chronology is not identical (e.g. "ISOChronology[Europe/Oslo]" vs "GJChronology[+02:00]")
        if ( ( dateTime1 == null ) || ( dateTime2 == null ) )
        {
            return dateTime1 == dateTime2;
        }

        return dateTime1.isEqual( dateTime2 ) &&
            ( dateTime1.getZone().getOffset( dateTime1 ) == dateTime2.getZone().getOffset( dateTime2 ) );
    }
}
