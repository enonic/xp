module api.data.type {

    export class GeoPointValueType extends ValueType {

        constructor() {
            super("GeoPoint");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.util.GeoPoint)) {
                return false;
            }
            return true;
        }

        isConvertible(value: string): boolean {
            if (api.util.isStringBlank(value)) {
                return false;
            }
            return api.util.GeoPoint.isValidString(value);
        }

        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }

            if (!this.isConvertible(value)) {
                return this.newNullValue();
            }
            return new Value(api.util.GeoPoint.fromString(value), this);
        }

        valueToString(value: Value): string {
            if (value.isNotNull()) {
                return value.getGeoPoint().toString();
            }
            else {
                return null;
            }
        }

        toJsonValue(value: api.data.Value): any {
            if (value.isNotNull()) {
                return value.getGeoPoint().toString();
            }
            else {
                return null;
            }
        }
    }
}