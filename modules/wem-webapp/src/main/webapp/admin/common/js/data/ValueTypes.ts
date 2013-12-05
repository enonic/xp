module api_data {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new ValueType("Data");

        static STRING = new ValueType("String");

        static BINARY_ID = new ValueType("BinaryId");

        static HTML_PART = new ValueType("HtmlPart");

        static XML = new ValueType("Xml");

        static DATE_MIDNIGHT = new ValueType("DateMidnight");

        static DATE_TIME = new ValueType("DateTime");

        static CONTENT_ID = new ValueType("ContentId");

        static LONG = new ValueType("Long");

        static DOUBLE = new ValueType("Double");

        static GEO_POINT = new ValueType("GeoPoint");

        static ATTACHMENT_NAME = new ValueType("AttachmentName");

        static ENTITY_ID = new ValueType("EntityId");

        static ALL: ValueType[] = [
            ValueTypes.DATA,
            ValueTypes.STRING,
            ValueTypes.DATE_TIME,
            ValueTypes.BINARY_ID,
            ValueTypes.HTML_PART,
            ValueTypes.XML,
            ValueTypes.DATE_MIDNIGHT,
            ValueTypes.DATE_TIME,
            ValueTypes.CONTENT_ID,
            ValueTypes.LONG,
            ValueTypes.DOUBLE,
            ValueTypes.GEO_POINT,
            ValueTypes.ATTACHMENT_NAME,
            ValueTypes.ENTITY_ID];

        public static fromName(name: string): ValueType {
            var match = null;
            ValueTypes.ALL.forEach((valueType: ValueType) => {
                if (valueType.toString() == name) {
                    match = valueType;
                }
            });

            api_util.assertNotNull(match, "Uknown ValueType: " + name);
            return match;
        }
    }
}