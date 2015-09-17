module api.ui.text {

    export class PasswordInput extends api.dom.InputEl {

        constructor(className?: string) {
            super(className, "password");

            this.addClass('password-input');
        }

    }
}