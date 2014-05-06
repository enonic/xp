module api.data {

    export class ValueType implements api.Equitable {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString() {
            return this.name;
        }

        valueToString(value: Value): string {
            return <string>value.asObject();
        }

        newValue(value: string) {
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