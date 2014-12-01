module api.data {

    export class ValueTypeContentId extends ValueType {

        constructor() {
            super("ContentId");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.content.ContentId)) {
                return false;
            }
            return true;
        }

        isConvertible(value: string): boolean {
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            return api.content.ContentId.isValidContentId(value);
        }

        newValue(value: string): Value {
            if (this.isConvertible(value)) {
                return new Value(new api.content.ContentId(value), this);
            }
            else {
                return this.newNullValue();
            }
        }

        valueToString(value: Value): string {
            if (value.isNotNull()) {
                return value.getContentId().toString();
            }
            else {
                return null;
            }
        }

        toJsonValue(value: Value): any {
            return value.isNull() ? null : value.getContentId().toString();
        }

        valueEquals(a: api.content.ContentId, b: api.content.ContentId): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}