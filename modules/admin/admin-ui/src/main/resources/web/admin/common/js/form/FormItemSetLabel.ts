module api.form {

    export class FormItemSetLabel extends api.dom.DivEl {

        private formItemSet:FormItemSet;

        constructor(formItemSet:FormItemSet) {
            super("form-item-set-label");

            this.formItemSet = formItemSet;

            var nodes:Node[] = [];

            var dragHandle = new api.dom.SpanEl( "drag-handle" );
            dragHandle.setHtml( ":::" );
            nodes.push( dragHandle.getHTMLElement() );

            nodes.push(document.createTextNode(formItemSet.getLabel()));

            if( formItemSet.getOccurrences().required() ) {
                nodes.push( document.createTextNode(" ") );
                var requiredMarker = new api.dom.SpanEl("required");
                nodes.push( requiredMarker.getHTMLElement() );
            }
            nodes.push( document.createTextNode(":") );
            this.getEl().appendChildren(nodes);
        }
    }
}