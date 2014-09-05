module api.data.type {

    export class LocalDateValueType extends ValueType {

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
            if (api.util.isStringBlank(value)) {
                return false;
            }

            var asString = <string>value;
            if (asString.length != 10) {
                return false;
            }
            // 2010-01-01
            if (!(asString.charAt(4) == '-' && asString.charAt(7) == '-')) {
                return false;
            }
            return this.isValid(new Date(asString));
        }

        newValue(value: string): Value {
            if (!this.isConvertible(value)) {
                return null;
            }
            var date = api.util.DateHelper.parseUTCDate(value);
            return new Value(date, this);
        }

        toJsonValue(value: api.data.Value): string {
            return api.util.DateHelper.formatUTCDate(value.getDate());
        }

        valueToString(value: Value): string {
            return (<Number>value.getObject()).toString();
        }
    }
}