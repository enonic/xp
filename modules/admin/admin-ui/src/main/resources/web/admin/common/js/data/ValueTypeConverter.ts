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
                return this.convertToData(this.value);
            } else if (toType === ValueTypes.STRING) {
                return this.convertToString(this.value);
            } else if (toType === ValueTypes.XML) {
                return this.convertToXml(this.value);
            } else if (toType === ValueTypes.LOCAL_DATE) {
                return this.convertToLocalDate(this.value);
            } else if (toType === ValueTypes.LOCAL_TIME) {
                return this.convertToLocalTime(this.value);
            } else if (toType === ValueTypes.LOCAL_DATE_TIME) {
                return this.convertToLocalDateTime(this.value);
            } else if (toType === ValueTypes.DATE_TIME) {
                return this.convertToDateTime(this.value);
            } else if (toType === ValueTypes.LONG) {
                return this.convertToLong(this.value);
            } else if (toType === ValueTypes.BOOLEAN) {
                return this.convertToBoolean(this.value.getObject());
            } else if (toType === ValueTypes.DOUBLE) {
                return this.convertToDouble(this.value);
            } else if (toType === ValueTypes.GEO_POINT) {
                return this.convertToGeoPoint(this.value);
            } else if (toType === ValueTypes.REFERENCE) {
                return this.convertToReference(this.value);
            } else if (toType === ValueTypes.BINARY_REFERENCE) {
                return this.convertToBinaryReference(this.value);
            }

            throw("Unknown ValueType: " + toType);
        }

        private convertToString(value: Value): Value {
            return ValueTypes.STRING.newValue(value.getString());
        }

        private convertToBoolean(value: any): Value {
            if (typeof value == "boolean") {
                return ValueTypes.BOOLEAN.newBoolean(value);
            } else if (typeof value == "string") {
                return ValueTypes.BOOLEAN.newValue(value);
            }
            return ValueTypes.BOOLEAN.newNullValue();
        }

        private convertToLong(value: Value): Value {
            if (value.getType() === ValueTypes.STRING) {
                return ValueTypes.LONG.newValue(value.getString());
            } else if (value.getType() === ValueTypes.DOUBLE) {
                return new Value(Math.floor(value.getDouble()), ValueTypes.LONG);
            } else if (value.getType() === ValueTypes.BOOLEAN) {
                if (value.getBoolean()) {
                    return ValueTypes.LONG.newValue('1');
                }
                return ValueTypes.LONG.newValue('0');
            } else if (value.getType() === ValueTypes.LONG) {
                return value;
            }
            return ValueTypes.LONG.newNullValue();
        }

        private convertToDouble(value: Value): Value {
            if (value.getType() === ValueTypes.STRING) {
                return ValueTypes.DOUBLE.newValue(value.getString());
            } else if (value.getType() === ValueTypes.LONG) {
                return new Value(value.getLong(), ValueTypes.LONG);
            } else if (value.getType() === ValueTypes.BOOLEAN) {
                if (value.getBoolean()) {
                    return ValueTypes.DOUBLE.newValue('1');
                }
                return ValueTypes.DOUBLE.newValue('0');
            } else if (value.getType() === ValueTypes.DOUBLE) {
                return value;
            }
            return ValueTypes.DOUBLE.newNullValue();
        }

        private convertToGeoPoint(value: Value): Value {
            if (value.getType() === ValueTypes.STRING) {
                return ValueTypes.GEO_POINT.newValue(value.getString());
            } else if (value.getType() === ValueTypes.GEO_POINT) {
                return value;
            }
            return ValueTypes.GEO_POINT.newNullValue();
        }

        private convertToReference(value: Value): Value {
            return ValueTypes.REFERENCE.newValue(value.getString());
        }

        private convertToBinaryReference(value: Value): Value {
            return ValueTypes.BINARY_REFERENCE.newValue(value.getString());
        }

        private convertToXml(value: Value): Value {
            return ValueTypes.XML.newValue(value.getString());
        }

        private convertToData(value: Value): Value {
            if (value.getType() === ValueTypes.DATA) {
                return value;
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(value.getObject, PropertySet)) {
                return new Value(value.getObject(), ValueTypes.DATA);
            }
            var propertySet = new PropertySet();
            propertySet.addProperty("converted " + value.getType().toString(), value)
            return new Value(propertySet, ValueTypes.DATA);
        }

        private convertToLocalDate(value: Value): Value {
            if (value.getType() === ValueTypes.STRING && ValueTypes.LOCAL_DATE.isConvertible(value.getString())) { // from string
                return ValueTypes.LOCAL_DATE.newValue(value.getString());
            } else if (value.getType() === ValueTypes.LOCAL_DATE_TIME && value.isNotNull()) { // from LocalDateTime
                var localDateTime = value.getString();
                return ValueTypes.LOCAL_DATE.newValue(localDateTime.substr(0, 10));
            } else if (value.getType() === ValueTypes.DATE_TIME && value.isNotNull()) { // from DateTime
                var localDate = value.getString();
                return ValueTypes.LOCAL_DATE.newValue(localDate.substr(0, 10));
            } else if (value.getType() === ValueTypes.LOCAL_DATE) {
                return value;
            }
            return ValueTypes.LOCAL_DATE.newNullValue();
        }

        private convertToLocalDateTime(value: Value): Value {
            if (value.getType() === ValueTypes.STRING && ValueTypes.LOCAL_DATE_TIME.isConvertible(value.getString())) { // from string
                return ValueTypes.LOCAL_DATE_TIME.newValue(value.getString());
            } else if (value.getType() === ValueTypes.LOCAL_DATE && value.isNotNull()) { // from LocalDate
                var localDate = <api.util.LocalDate>value.getObject();
                return new Value(api.util.LocalDateTime.fromString(localDate.toString() + "T00:00:00"), ValueTypes.LOCAL_DATE_TIME);
            } else if (value.getType() === ValueTypes.DATE_TIME && value.isNotNull()) { // from DateTime
                var dateTime = value.getString();
                return ValueTypes.LOCAL_DATE_TIME.newValue(dateTime.substr(0, 19));
            } else if (value.getType() === ValueTypes.LOCAL_DATE_TIME) {
                return value;
            }
            return ValueTypes.LOCAL_DATE_TIME.newNullValue();
        }

        private convertToDateTime(value: Value): Value {
            if (value.getType() === ValueTypes.STRING && ValueTypes.DATE_TIME.isConvertible(value.getString())) { // from string
                return ValueTypes.DATE_TIME.newValue(value.getString());
            } else if (value.getType() === ValueTypes.LOCAL_DATE && value.isNotNull()) { // from LocalDate
                return ValueTypes.DATE_TIME.newValue(value.getString() + "T00:00:00+00:00");
            } else if (value.getType() === ValueTypes.LOCAL_DATE_TIME && value.isNotNull()) { // from LocalDateTime
                var dateTime = value.getString();
                return ValueTypes.DATE_TIME.newValue(dateTime);
            } else if (value.getType() === ValueTypes.DATE_TIME) {
                return value;
            }
            return ValueTypes.DATE_TIME.newNullValue();
        }

        private convertToLocalTime(value: Value): Value {
            if (value.getType() === ValueTypes.STRING && ValueTypes.LOCAL_TIME.isConvertible(value.getString())) { // from string
                return ValueTypes.LOCAL_TIME.newValue(value.getString());
            } else if (value.getType() === ValueTypes.LOCAL_DATE_TIME && value.isNotNull()) { // from LocalDateTime
                var localDateTime = <Date>value.getObject();
                return ValueTypes.LOCAL_TIME.newValue(localDateTime.getHours() + ":" + localDateTime.getMinutes() + ":" +
                                                      localDateTime.getSeconds());
            } else if (value.getType() === ValueTypes.DATE_TIME && value.isNotNull()) { // from DateTime
                var dateTime = <api.util.DateTime> value.getObject();
                return ValueTypes.LOCAL_TIME.newValue(dateTime.getHours() + ":" + dateTime.getMinutes() + ":" + dateTime.getSeconds());
            } else if (value.getType() === ValueTypes.LOCAL_TIME) {
                return value;
            }
            return ValueTypes.LOCAL_TIME.newNullValue();
        }

    }
}