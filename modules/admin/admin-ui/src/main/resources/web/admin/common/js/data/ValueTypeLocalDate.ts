module api.data {

    import LocalDate = api.util.LocalDate;

    export class ValueTypeLocalDate extends ValueType {

        constructor() {
            super("LocalDate");
        }

        isValid(value: any): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(value, LocalDate)) {
                return true;
            }

            return LocalDate.isValidISODateString(value);
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
            return this.isValid(value);
        }

        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }
            if (!this.isConvertible(value)) {
                return this.newNullValue();
            }
            let date = LocalDate.fromISOString(value);
            return new Value(date, this);
        }

        toJsonValue(value: Value): string {
            return value.isNull() ? null : value.getLocalDate().toString();
        }

        valueToString(value: Value): string {
            return value.getLocalDate().toString();
        }

        valueEquals(a: LocalDate, b: LocalDate): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}
