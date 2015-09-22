module api.data {

    import DateTime = api.util.DateTime;

    export class ValueTypeDateTime extends ValueType {

        constructor() {
            super("DateTime");
        }

        isValid(value: any): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(value, api.util.DateTime)) {
                return true;
            }
            return api.util.DateTime.isValidDateTime(value);
        }

        isConvertible(value: string): boolean {
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            // 2010-01-01T10:55:00+01:00
            if (value.length < 19) {
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
            var date = DateTime.fromString(value);
            return new Value(date, this);
        }

        // 2010-01-01T10:55:00+01:00
        toJsonValue(value: Value): string {
            return value.isNull() ? null : value.getDateTime().toString();
        }

        valueToString(value: Value): string {
            return value.getDateTime().toString();
        }

        valueEquals(a: DateTime, b: DateTime): boolean {
            return api.ObjectHelper.equals(a, b);
        }

    }
}