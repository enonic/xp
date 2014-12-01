module api.data {

    export class ValueTypePropertySet extends ValueType {

        constructor() {
            super("PropertySet");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, PropertySet)) {
                return false;
            }
            return true;
        }

        isConvertible(value: string): boolean {
            return false;
        }

        newValue(value: string): Value {
            throw new Error("A value of type Data cannot be created from a string");
        }

        toJsonValue(value: Value): any {
            if (value.isNull()) {
                return null;
            }
            var data = <PropertySet>value.getObject();
            return data.toJson();
        }

        fromJsonValue(propertyArrayJsonArray: PropertyArrayJson[]): Value {
            throw new Error("Method not supported!");
        }

        valueToString(value: Value): string {
            throw new Error("A value of type Data cannot be made into a string");
        }

        valueEquals(a: PropertySet, b: PropertySet): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}