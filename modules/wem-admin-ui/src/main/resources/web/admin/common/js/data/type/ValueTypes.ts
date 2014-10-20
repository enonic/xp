module api.data.type {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new DataValueType();

        static STRING = new StringValueType();

        static HTML_PART = new HtmlPartValueType();

        static XML = new XmlValueType();

        static LOCAL_DATE = new LocalDateValueType();

        static LOCAL_TIME = new LocalTimeValueType();

        static LOCAL_DATE_TIME = new LocalDateTimeValueType();

        static DATE_TIME = new DateTimeValueType();

        static CONTENT_ID = new ContentIdValueType();

        static LONG = new LongValueType();

        static BOOLEAN = new BooleanValueType();

        static DOUBLE = new DoubleValueType();

        static GEO_POINT = new GeoPointValueType();

        static NODE_ID = new ValueType("NodeId");

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
            ValueTypes.GEO_POINT,
            ValueTypes.NODE_ID];

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