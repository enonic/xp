module api.ui.selector {

    export class DropdownGridMultiselectEvent {

        private rows: number[];

        constructor(rows: number[]) {
            this.rows = rows;
        }

        getRows(): number[] {
            return this.rows;
        }

    }
}
