module api.data {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA: ValueTypePropertySet = new ValueTypePropertySet();

        static STRING: ValueTypeString = new ValueTypeString();

        static XML: ValueTypeXml = new ValueTypeXml();

        static LOCAL_DATE: ValueTypeLocalDate = new ValueTypeLocalDate();

        static LOCAL_TIME: ValueTypeLocalTime = new ValueTypeLocalTime();

        static LOCAL_DATE_TIME: ValueTypeLocalDateTime = new ValueTypeLocalDateTime();

        static DATE_TIME: ValueTypeDateTime = new ValueTypeDateTime();

        static LONG: ValueTypeLong = new ValueTypeLong();

        static BOOLEAN: ValueTypeBoolean = new ValueTypeBoolean();

        static DOUBLE: ValueTypeDouble = new ValueTypeDouble();

        static GEO_POINT: ValueTypeGeoPoint = new ValueTypeGeoPoint();

        static REFERENCE: ValueTypeReference = new ValueTypeReference();

        static BINARY_REFERENCE: ValueTypeBinaryReference = new ValueTypeBinaryReference();

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
            ValueTypes.BINARY_REFERENCE,
        ];

        public static fromName(name: string): ValueType {
            for (let i = 0; i < ValueTypes.ALL.length; i++) {
                let type = ValueTypes.ALL[i];
                if (type.toString() === name) {
                    return type;
                }
            }
            throw('Unknown ValueType: ' + name);
        }
    }
}
