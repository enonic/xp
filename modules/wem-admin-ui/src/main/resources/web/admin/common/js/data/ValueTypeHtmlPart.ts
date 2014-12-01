module api.data {

    export class ValueTypeHtmlPart extends ValueType {

        constructor() {
            super("HtmlPart");
        }

        isValid(value: any): boolean {
            return typeof value === 'string';
        }

        valueEquals(a: string, b: string): boolean {
            return api.ObjectHelper.stringEquals(a, b);
        }
    }
}