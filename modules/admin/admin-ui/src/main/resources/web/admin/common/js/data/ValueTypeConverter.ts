module api.data {

    export class ValueTypeConverter {

        private value: Value;

        constructor(value: Value) {
            this.value = value;
        }

        convertTo(toType: ValueType): Value {

            if (this.value.getType() === toType) {
                return this.value;
            }

            if (toType === ValueTypes.DATA) {

            } else if (toType === ValueTypes.STRING) {

            } else if (toType === ValueTypes.XML) {

            } else if (toType === ValueTypes.LOCAL_DATE) {

            } else if (toType === ValueTypes.LOCAL_TIME) {

            } else if (toType === ValueTypes.LOCAL_DATE_TIME) {

            } else if (toType === ValueTypes.DATE_TIME) {

            } else if (toType === ValueTypes.LONG) {

            } else if (toType === ValueTypes.BOOLEAN) {
                return this.convertToBoolean(this.value.getObject());

            } else if (toType === ValueTypes.DOUBLE) {

            } else if (toType === ValueTypes.GEO_POINT) {

            } else if (toType === ValueTypes.REFERENCE) {

            } else if (toType === ValueTypes.BINARY_REFERENCE) {

            }

            throw("Unknown ValueType: " + toType);
        }

        private convertToBoolean(value: any): Value {
            if (typeof value == "boolean") {
                return ValueTypes.BOOLEAN.newBoolean(value);
            } else if (typeof value == "string") {
                return ValueTypes.BOOLEAN.newValue(value);
            }
            return ValueTypes.BOOLEAN.newNullValue();
        }

    }
}