module api.data {

    export class ValueTypeGeoPoint extends ValueType {

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
            if (api.util.StringHelper.isBlank(value)) {
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

        toJsonValue(value: Value): any {
            return value.isNull() ? null : value.getGeoPoint().toString();
        }

        valueEquals(a: api.util.GeoPoint, b: api.util.GeoPoint): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}