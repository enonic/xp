module api.ui.button {

    export class CloseButton extends api.ui.button.Button {

        constructor() {
            super();
            this.addClass('close-button icon-medium icon-close2');
        }
    }

}