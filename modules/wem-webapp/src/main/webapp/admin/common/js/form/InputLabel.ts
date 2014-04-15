module api.form {

    export class InputLabel extends api.dom.DivEl {

        private input:Input;

        constructor(input:Input) {
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