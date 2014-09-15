module api.data {

    import ValueType = api.data.type.ValueType;
    import ValueTypes = api.data.type.ValueTypes;

    export class Value implements api.Equitable, api.Cloneable {

        private value: Object;

        private type: ValueType;

        constructor(value: Object, type: ValueType) {
            this.value = value;
            this.type = type;
            if (value) {
                var isValid = this.type.isValid(value);
                if (isValid == undefined) {
                    throw new Error(api.util.getClassName(this.type) + ".isValid() did not return any value: " + isValid);
                }
                if (isValid == false) {
                    throw new Error("Invalid value for type " + type.toString() + ": " + value);
                }
            }
        }

        isNotNull(): boolean {
            return !this.isNull();
        }

        isNull(): boolean {
            return this.value == null || this.value == undefined;
        }

        getObject(): Object {
            return this.value;
        }

        getDate(): Date {
            if (this.isNull()) {
                return null;
            }
            return <Date>this.value;
        }

        getGeoPoint(): api.util.GeoPoint {
            if (this.isNull()) {
                return null;
            }
            return <api.util.GeoPoint>this.value;
        }

        getLocalTime(): api.util.LocalTime {
            if (this.isNull()) {
                return null;
            }
            return <api.util.LocalTime>this.value;
        }

        getContentId(): api.content.ContentId {
            if (this.isNull()) {
                return null;
            }
            return <api.content.ContentId>this.value;
        }

        getBoolean(): boolean {
            if (this.isNull()) {
                return null;
            }
            return <boolean>this.value;
        }

        getNumber(): number {
            if (this.isNull()) {
                return null;
            }
            return <number><number>this.value;
        }

        asString(): string {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToString(this);
        }

        asNumber(): number {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToNumber(this);
        }

        asBoolean(): boolean {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToBoolean(this);
        }

        isRootDataSet(): boolean {
            return ValueTypes.DATA.toString() == this.type.toString();
        }

        asRootDataSet(): RootDataSet {
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(this.value, RootDataSet),
                    "Expected value to be a RootDataSet: " + api.util.getClassName(this.value));
            return <RootDataSet>this.value;
        }

        getType(): ValueType {
            return this.type;
        }

        setValue(value: Object) {
            this.value = value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Value)) {
                return false;
            }

            var other = <Value>o;

            if (!api.ObjectHelper.objectEquals(this.value, other.value)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.type, other.type)) {
                return false;
            }

            return true;
        }

        clone(): Value {

            return new Value(this.value, this.type);
        }
    }
}