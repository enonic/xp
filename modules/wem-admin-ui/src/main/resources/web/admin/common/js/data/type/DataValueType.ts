module api.data.type {

    export class DataValueType extends ValueType {

        constructor() {
            super("Data");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.data.RootDataSet)) {
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

        toJsonValue(value: api.data.Value): any {
            var data = <api.data.RootDataSet>value.getObject();
            return data.toJson();
        }

        fromJsonValue(jsonData: api.data.json.DataJson[]): api.data.Value {
            var rootDataSet = api.data.DataFactory.createRootDataSet(jsonData);
            return new Value(rootDataSet, this);
        }

        valueToString(value: Value): string {
            throw new Error("A value of type Data cannot be made into a string");
        }
    }
}