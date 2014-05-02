module api.form {

    export class InputLabel extends api.dom.DivEl {

        private input:Input;

        constructor(input:Input) {
            super("input-label");

            this.input = input;

            var nodes:Node[] = [];

            var label = new api.dom.SpanEl("label");
            label.getEl().setInnerHtml(input.getLabel());
            label.getEl().setAttribute('title', input.getLabel());
            nodes.push(label.getHTMLElement());

            if( input.getOccurrences().required() ) {
                nodes.push( document.createTextNode(" ") );
                var requiredMarker = new api.dom.SpanEl("required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            this.getEl().appendChildren(nodes);
        }
    }
}