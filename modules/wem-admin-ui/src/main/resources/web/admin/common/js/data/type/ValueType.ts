module api.data.type {

    export class ValueType implements api.Equitable {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString(): string {
            return this.name;
        }

        valueToString(value: api.data.Value): string {
            return String(value.getObject());
        }

        valueToBoolean(value: api.data.Value): boolean {
            return value.asString() == "true";
        }

        valueToNumber(value: api.data.Value): number {
            return Number(value.getObject());
        }

        isValid(value: any): boolean {
            return true;
        }

        isConvertible(value: string): boolean {
            return true;
        }

        newValue(value: string): api.data.Value {
            return new Value(value, this);
        }

        newNullValue(): api.data.Value {
            return new Value(null, this);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ValueType)) {
                return false;
            }

            var other = <ValueType>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }

        valueEquals(a: any, b: any): boolean {
            return api.ObjectHelper.objectEquals(a, b);
        }

        /**
         * Returns the actual object backing this Value.
         * If the REST service or JSON would not understand this value, then override and return compatible value.
         */
        toJsonValue(value: api.data.Value): any {
            return value.getObject();
        }

        fromJsonValue(jsonValue: any): api.data.Value {
            if (jsonValue) {
                return this.newValue(jsonValue.toString());
            }
            else {
                return this.newNullValue();
            }
        }
    }
}