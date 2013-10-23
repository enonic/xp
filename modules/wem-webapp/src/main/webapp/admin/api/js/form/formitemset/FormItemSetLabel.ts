module api_form_formitemset {

    export class FormItemSetLabel extends api_dom.DivEl {

        private formItemSet:api_form.FormItemSet;

        constructor(formItemSet:api_form.FormItemSet) {
            super("FormItemSetLabel", "form-item-set-label");

            this.formItemSet = formItemSet;

            var nodes:Node[] = [];
            nodes.push(new api_dom.TextNode(formItemSet.getLabel()).getText());

            if( formItemSet.getOccurrences().required() ) {
                nodes.push( new api_dom.TextNode(" ").getText() );
                var requiredMarker = new api_dom.SpanEl(null, "required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            nodes.push( new api_dom.TextNode(":").getText() );
            this.getEl().appendChildren(nodes);
        }
    }
}