module api.ui.button {

    export class CloseButton extends api.ui.button.Button {

        constructor(className?: string) {
            super();
            this.addClass('close-button icon-medium icon-close2');
            if (className) {
                this.addClass(className);
            }
        }
    }

}