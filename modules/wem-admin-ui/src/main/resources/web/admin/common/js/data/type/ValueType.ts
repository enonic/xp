module api.data.type {

    import Value = api.data.Value;

    export class ValueType implements api.Equitable {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString(): string {
            return this.name;
        }

        valueToString(value: Value): string {
            return String(value.getObject());
        }

        valueToBoolean(value: Value): boolean {
            return value.asString() == "true";
        }

        valueToNumber(value: Value): number {
            return Number(value.getObject());
        }

        isValid(value: any): boolean {
            return true;
        }

        isConvertible(value: any): boolean {
            return true;
        }

        newValue(value: string): Value {
            return new Value(value, this);
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
    }
}