module api.data {

    export class ValueTypeLocalDate extends ValueType {

        constructor() {
            super("LocalDate");
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

            if (value.length != 10) {
                return false;
            }
            if (!(value.charAt(4) == '-' && value.charAt(7) == '-')) {
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
            var date = api.util.DateHelper.parseUTCDate(value);
            return new Value(date, this);
        }

        toJsonValue(value: Value): string {
            return value.isNull() ? null : api.util.DateHelper.formatUTCDate(value.getDate());
        }

        valueToString(value: Value): string {
            return api.util.DateHelper.formatUTCDate((<Date>value.getObject()));
        }

        valueEquals(a: Date, b: Date): boolean {
            return api.ObjectHelper.dateEquals(a, b);
        }
    }
}