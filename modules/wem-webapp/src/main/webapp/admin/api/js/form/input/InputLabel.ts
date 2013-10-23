module api_form_input {

    export class InputLabel extends api_dom.DivEl {

        private input:api_form.Input;

        constructor(input:api_form.Input) {
            super("InputLabel", "input-label");

            this.input = input;

            var nodes:Node[] = [];
            nodes.push(new api_dom.TextNode(input.getLabel()).getText());

            if( input.getOccurrences().required() ) {
                nodes.push( new api_dom.TextNode(" ").getText() );
                var requiredMarker = new api_dom.SpanEl(null, "required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            this.getEl().appendChildren(nodes);
        }
    }
}