module api.form.input {

    export class InputLabel extends api.dom.DivEl {

        private input:api.form.Input;

        constructor(input:api.form.Input) {
            super("input-label");

            this.input = input;

            var nodes:Node[] = [];
            nodes.push(document.createTextNode(input.getLabel()));

            if( input.getOccurrences().required() ) {
                nodes.push( document.createTextNode(" ") );
                var requiredMarker = new api.dom.SpanEl("required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            this.getEl().appendChildren(nodes);
        }
    }
}