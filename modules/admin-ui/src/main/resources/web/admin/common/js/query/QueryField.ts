module api.query {

    export class QueryField {

        static DISPLAY_NAME: string = "displayName";

        static NAME: string = "_name";

        static ALL: string = "_alltext";

        static MODIFIED_TIME: string = "_modifiedTime"

        static MANUAL_ORDER_VALUE: string = "_manualOrderValue";

        static WEIGHT_SEPARATOR: string = "^";

        static CONTENT_TYPE: string = "type";

        weight: number;

        name: string;

        constructor(name: string, weight?: number) {
            this.name = name;
            if (weight) {
                this.weight = weight;
            }
        }

        toString() {
            if (this.weight) {
                return this.name + QueryField.WEIGHT_SEPARATOR + this.weight;
            } else {
                return this.name;
            }
        }
    }

}