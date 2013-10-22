module api_form_input {

    export class InputLabel extends api_dom.DivEl {

        private input:api_form.Input;

        constructor(input:api_form.Input) {
            super("InputLabel", "input-label");

            this.input = input;

            this.getEl().setInnerHtml(input.getLabel());
        }
    }
}