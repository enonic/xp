module api.form.formitemset {

    export class FormItemSetLabel extends api.dom.DivEl {

        private formItemSet:api.form.FormItemSet;

        constructor(formItemSet:api.form.FormItemSet) {
            super("FormItemSetLabel", "form-item-set-label");

            this.formItemSet = formItemSet;

            var nodes:Node[] = [];
            nodes.push(document.createTextNode(formItemSet.getLabel()));

            if( formItemSet.getOccurrences().required() ) {
                nodes.push( document.createTextNode(" ") );
                var requiredMarker = new api.dom.SpanEl(null, "required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            nodes.push( document.createTextNode(":") );
            this.getEl().appendChildren(nodes);
        }
    }
}