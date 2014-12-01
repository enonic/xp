module api.data {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new ValueTypePropertySet();

        static STRING = new ValueTypeString();

        static HTML_PART = new ValueTypeHtmlPart();

        static XML = new ValueTypeXml();

        static LOCAL_DATE = new ValueTypeLocalDate();

        static LOCAL_TIME = new ValueTypeLocalTime();

        static LOCAL_DATE_TIME = new ValueTypeLocalDateTime();

        static DATE_TIME = new ValueTypeDateTime();

        static CONTENT_ID = new ValueTypeContentId();

        static LONG = new ValueTypeLong();

        static BOOLEAN = new ValueTypeBoolean();

        static DOUBLE = new ValueTypeDouble();

        static GEO_POINT = new ValueTypeGeoPoint();

        static ALL: ValueType[] = [
            ValueTypes.DATA,
            ValueTypes.STRING,
            ValueTypes.HTML_PART,
            ValueTypes.XML,
            ValueTypes.LOCAL_DATE,
            ValueTypes.LOCAL_TIME,
            ValueTypes.LOCAL_DATE_TIME,
            ValueTypes.DATE_TIME,
            ValueTypes.CONTENT_ID,
            ValueTypes.LONG,
            ValueTypes.BOOLEAN,
            ValueTypes.DOUBLE,
            ValueTypes.GEO_POINT];

        public static fromName(name: string): ValueType {
            for (var i = 0; i < ValueTypes.ALL.length; i++) {
                var type = ValueTypes.ALL[i];
                if (type.toString() == name) {
                    return type;
                }
            }
            throw("Unknown ValueType: " + name);
        }
    }
}