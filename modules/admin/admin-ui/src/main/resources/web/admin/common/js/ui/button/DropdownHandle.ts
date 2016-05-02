module api.ui.button {

    export class DropdownHandle extends api.dom.DivEl {

        constructor() {
            super("dropdown-handle");

            this.setEnabled(true);
            this.removeClass('down');
        }

        setEnabled(value: boolean) {
            if (value) {
                this.removeClass('disabled');
            } else {
                this.addClass('disabled');
            }
        }
        
        isEnabled(): boolean {
            return !this.hasClass('disabled');
        }

        down() {
            this.addClass('down');
        }

        up() {
            this.removeClass('down')
        }
    }
}
