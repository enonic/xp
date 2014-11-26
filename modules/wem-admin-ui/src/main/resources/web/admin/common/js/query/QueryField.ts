module api.query {

    export class QueryField {

        static DISPLAY_NAME = "displayName";

        static NAME = "_name";

        static ALL = "_all_text";

        static MODIFIED_TIME = "_modifiedTime"

        static MANUAL_ORDER_VALUE = "_manualOrderValue";

        static WEIGHT_SEPARATOR: string = "^";

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