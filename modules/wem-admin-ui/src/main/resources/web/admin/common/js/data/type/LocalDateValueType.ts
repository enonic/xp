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
            return !isNaN(valueAsDate.getTime());
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
            return new Value(this.convertFromString(value), this);
        }

        toJsonValue(value: api.data.Value): string {
            var date = value.getDate();
            var yearAsString = "" + date.getUTCFullYear();
            var monthAsString = "" + (date.getUTCMonth() + 1);
            if (monthAsString.length == 1) {
                monthAsString = "0" + monthAsString;
            }
            var dateAsString = "" + date.getUTCDate();
            if (dateAsString.length == 1) {
                dateAsString = "0" + dateAsString;
            }
            return yearAsString + "-" + monthAsString + "-" + dateAsString;
        }

        private convertFromString(value: string): Date {

            var parsedYear: number = Number(value.substring(0, 4));
            var parsedMonth: number = Number(value.substring(5, 7));
            var parsedDayOfMonth: number = Number(value.substring(8, 10));
            return api.util.DateHelper.newUTCDate(parsedYear, parsedMonth - 1, parsedDayOfMonth);
        }

        valueToString(value: Value): string {
            return (<Number>value.getObject()).toString();
        }
    }
}