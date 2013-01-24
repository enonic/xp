package com.enonic.wem.migrate.account;

enum OldGroupType
{

    /**
     * Only one occurrence. Members of this group shall have any rights, regardless of what rights are set for this group.
     */
    ENTERPRISE_ADMINS( 0, "Enterprise Administrators", true, true, true ),

    // any occurrence per user store
    USERSTORE_GROUP( 1, "userStoreGroup", false, false, false ),

    // one occurrence per user store
    USERSTORE_ADMINS( 2, "Userstore Administrators", true, false, false ),

    /**
     * One occurrence per user store. All users except anonymous is implicit member of this group. So remember to do extra security checks on
     * this group.
     */
    AUTHENTICATED_USERS( 3, "Authenticated Users", true, false, false ),

    // any number of occurrence
    GLOBAL_GROUP( 4, "globalGroup", false, false, true ),

    // only one occurrence
    ADMINS( 5, "Administrators", true, true, true ),

    // one occurrence per user
    USER( 6, "userGroup", false, false, false ),

    // only one occurrence
    ANONYMOUS( 7, "anonymousGroup", true, true, true ),

    // only one occurrence
    CONTRIBUTORS( 8, "Contributors", true, true, true ),

    // only one occurrence
    DEVELOPERS( 9, "Developers", true, true, true ),

    // only one occurrence
    EXPERT_CONTRIBUTORS( 10, "Expert Contributors", true, true, true );

    private Integer value;

    private String name;

    private boolean builtIn = false;

    private boolean onlyOneGroupOccurance = false;

    private boolean global = false;

    OldGroupType( int value, String name, boolean builtIn, boolean onlyOneGroupOccurance, boolean global )
    {
        this.value = value;
        this.name = name;
        this.builtIn = builtIn;
        this.onlyOneGroupOccurance = onlyOneGroupOccurance;
        this.global = global;
    }

    public static OldGroupType get( String groupType )
        throws NumberFormatException
    {

        return get( Integer.parseInt( groupType ) );
    }

    public static OldGroupType get( int value )
    {

        OldGroupType[] types = values();
        for ( OldGroupType type : types )
        {
            if ( type.toInteger() == value )
            {
                return type;
            }
        }

        return null;
    }


    public Integer toInteger()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    /**
     * Two group types are equal if their database values are equal.
     *
     * @param o The other <code>GroupType</code> to compare this to.
     * @return <code>true</code> if the other object is equal.  <code>false</code> otherwise.
     */
    public boolean equals( OldGroupType o )
    {
        return ( value.equals( o.value ) );
    }

    public String toString()
    {
        return getName() + ", DB value: " + value + ( builtIn ? " (built-in)" : "" );
    }
}
