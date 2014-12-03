module api.data {

    export class ValueTypeLocalDateTime extends ValueType {

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
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            // 2010-01-01T10:55:00
            if (value.length != 19) {
                return false;
            }
            var valueAsDate = api.util.DateHelper.parseUTCDateTime(value);
            return this.isValid(valueAsDate);
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

        toJsonValue(value: Value): string {
            return value.isNull() ? null : api.util.DateHelper.formatUTCDateTime(value.getDate());
        }

        valueToString(value: Value): string {
            return api.util.DateHelper.formatUTCDateTime((<Date>value.getObject()));
        }

        valueEquals(a: Date, b: Date): boolean {
            return api.ObjectHelper.dateEquals(a, b);
        }
    }
}