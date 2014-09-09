module api.data.type {

    export class LocalDateTimeValueType extends ValueType {

        constructor() {
            super("LocalDateTime");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                // Date has object as typeof
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, Date)) {
                return false;
            }
            var valueAsDate = <Date>value;
            return !api.util.DateHelper.isInvalidDate(valueAsDate);
        }

        isConvertible(value: string): boolean {
            if (api.util.isStringBlank(value)) {
                return false;
            }
            // 2010-01-01T10:55:00
            if (value.length != 19) {
                return false;
            }
            if (!(value.charAt(4) == '-' && value.charAt(7) == '-')) {
                return false;
            }
            if (!(value.charAt(10) == 'T' && value.charAt(13) == ':' && value.charAt(16) == ':')) {
                return false;
            }
            return this.isValid(new Date(value));
        }

        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }
            if (!this.isConvertible(value)) {
                return this.newNullValue();
            }
            var date = api.util.DateHelper.parseUTCDateTime(value);
            return new Value(date, this);
        }

        toJsonValue(value: api.data.Value): string {
            return api.util.DateHelper.formatUTCDateTime(value.getDate());
        }

        valueToString(value: Value): string {
            return api.util.DateHelper.formatUTCDateTime((<Date>value.getObject()));
        }
    }
}