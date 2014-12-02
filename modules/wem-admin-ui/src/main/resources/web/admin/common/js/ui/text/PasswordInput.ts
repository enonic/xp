module api.ui.text {

    export class PasswordInput extends api.dom.InputEl {

        constructor(className?: string) {
            super(className, "password");

            this.addClass('password-input');
        }

        setPlaceholder(value: string): PasswordInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder(): string {
            return this.getEl().getAttribute('placeholder');
        }
    }
}