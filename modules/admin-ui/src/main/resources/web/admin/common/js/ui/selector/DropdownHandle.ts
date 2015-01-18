module api.ui.selector {

    export class DropdownHandle extends api.dom.DivEl {

        constructor() {
            super("dropdown-handle");

            this.setEnabled(true);
            this.up();
        }

        setEnabled(value: boolean) {
            if (value) {
                this.removeClass('disabled');
            }
            else {
                this.addClass('disabled');
            }
        }

        down() {
            this.addClass('down');
        }

        up() {
            this.removeClass('down')
        }
    }
}
