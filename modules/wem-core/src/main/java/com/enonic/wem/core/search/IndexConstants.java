package com.enonic.wem.core.search;

public class IndexConstants
{
    public final static String WEM_INDEX = "wem";

    public final static String FIELD_TYPE_SPERATATOR = ".";

    public final static String NUMBER_FIELD_POSTFIX = "number";

    public final static String DATE_FIELD_POSTFIX = "date";

    public final static String ALL_USERDATA_BASE = "_all_userdata";

    public final static String ALL_USERDATA_STRING_FIELD = ALL_USERDATA_BASE;

    public final static String ALL_USERDATA_NUMBER_FIELD = ALL_USERDATA_BASE + FIELD_TYPE_SPERATATOR + NUMBER_FIELD_POSTFIX;

    public final static String ALL_USERDATA_DATE_FIELD = ALL_USERDATA_BASE + FIELD_TYPE_SPERATATOR + DATE_FIELD_POSTFIX;

}

