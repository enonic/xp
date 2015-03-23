module api.data {

    export class ValueTypeDateTime extends ValueType {

        constructor() {
            super("DateTime");
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
            // 2010-01-01T10:55:00Z
            if (value.length != 20) {
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

        // 2010-01-01T10:55:00Z
        toJsonValue(value: Value): string {
            return value.isNull() ? null : api.util.DateHelper.formatUTCDateTime(value.getDateTime()) + 'Z';
        }

        valueToString(value: Value): string {
            return api.util.DateHelper.formatUTCDateTime((<Date>value.getObject()));
        }

        valueEquals(a: Date, b: Date): boolean {
            return api.ObjectHelper.dateEquals(a, b);
        }

    }
}