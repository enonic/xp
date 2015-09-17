module api.form {

    export class InputLabel extends api.dom.DivEl {

        private input:Input;

        constructor(input:Input) {
            super("input-label");

            this.input = input;

            var wrapper = new api.dom.DivEl("wrapper");
            var label = new api.dom.DivEl("label");
            label.getEl().setInnerHtml(input.getLabel());
            wrapper.getEl().appendChild(label.getHTMLElement());

            if( input.getOccurrences().required() ) {
                wrapper.addClass("required");
            }

            this.getEl().appendChild(wrapper.getHTMLElement());
        }
    }
}