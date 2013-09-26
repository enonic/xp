module app_wizard_form {

    export class FormItemOccurrenceView extends api_dom.DivEl {

        private index:number;

        // TODO: Never used or to be used?
        private data:api_data.Data;

        constructor(idPrefix:string, className, index:number, data?:api_data.Data) {
            super(idPrefix, className);
            this.index = index;
            this.data = data;
        }

        getIndex():number {
            return this.index;
        }

        hasData(): boolean {
            return this.data != null;
        }

        getData():api_data.Data {
            return this.data;
        }

        refresh() {
            throw new Error("Must be implemented by inheritor");
        }
    }
}