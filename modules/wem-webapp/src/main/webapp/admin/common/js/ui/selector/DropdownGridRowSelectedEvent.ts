module api.ui.selector {

    export class DropdownGridRowSelectedEvent {

        private row: number;

        constructor(row: number) {
            this.row = row;
        }

        getRow(): number {
            return this.row;
        }

    }
}
