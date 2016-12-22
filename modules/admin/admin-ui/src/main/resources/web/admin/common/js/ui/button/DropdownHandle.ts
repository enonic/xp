module api.ui.button {

    export class DropdownHandle extends api.dom.ButtonEl {

        constructor() {
            super("dropdown-handle");

            this.setEnabled(true);
            this.removeClass('down');
        }

        setEnabled(value: boolean) {
            this.toggleClass("disabled", !value);
        }

        isEnabled(): boolean {
            return !this.hasClass('disabled');
        }

        down() {
            this.addClass('down');
        }

        up() {
            this.removeClass('down');
        }
    }
}
