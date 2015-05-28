module api.data {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new ValueTypePropertySet();

        static STRING = new ValueTypeString();

        static XML = new ValueTypeXml();

        static LOCAL_DATE = new ValueTypeLocalDate();

        static LOCAL_TIME = new ValueTypeLocalTime();

        static LOCAL_DATE_TIME = new ValueTypeLocalDateTime();

        static DATE_TIME = new ValueTypeDateTime();

        static LONG = new ValueTypeLong();

        static BOOLEAN = new ValueTypeBoolean();

        static DOUBLE = new ValueTypeDouble();

        static GEO_POINT = new ValueTypeGeoPoint();

        static REFERENCE = new ValueTypeReference();

        static BINARY_REFERENCE = new ValueTypeBinaryReference();

        static ALL: ValueType[] = [
            ValueTypes.DATA,
            ValueTypes.STRING,
            ValueTypes.XML,
            ValueTypes.LOCAL_DATE,
            ValueTypes.LOCAL_TIME,
            ValueTypes.LOCAL_DATE_TIME,
            ValueTypes.DATE_TIME,
            ValueTypes.LONG,
            ValueTypes.BOOLEAN,
            ValueTypes.DOUBLE,
            ValueTypes.GEO_POINT,
            ValueTypes.REFERENCE,
            ValueTypes.BINARY_REFERENCE
        ];

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