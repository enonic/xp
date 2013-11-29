package com.enonic.wem.core.index;

public class IndexConstants
{
    public final static String FIELD_TYPE_SPERATATOR = ".";

    public final static String NUMBER_FIELD_POSTFIX = "number";

    public final static String DATE_FIELD_POSTFIX = "date";

    public final static String ALL_USERDATA_BASE = "_all_userdata";

    public final static String ANALYZER_VALUE_FIELD = "_document_analyzer";

    public final static String COLLECTION_FIELD = "_collection";

    public final static String DEFAULT_COLLECTION = "default";

    @Deprecated
    public final static String ALL_USERDATA_STRING_FIELD = ALL_USERDATA_BASE;

    @Deprecated
    public final static String ALL_USERDATA_NUMBER_FIELD = ALL_USERDATA_BASE + FIELD_TYPE_SPERATATOR + NUMBER_FIELD_POSTFIX;

    @Deprecated
    public final static String ALL_USERDATA_DATE_FIELD = ALL_USERDATA_BASE + FIELD_TYPE_SPERATATOR + DATE_FIELD_POSTFIX;

}

