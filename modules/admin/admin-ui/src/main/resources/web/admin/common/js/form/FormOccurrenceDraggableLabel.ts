module api.form {

    export class FormOccurrenceDraggableLabel extends api.dom.DivEl {

        constructor(label: string, occurrences: Occurrences) {
            super("form-occurrence-draggable-label");

            var nodes: Node[] = [];

            var dragHandle = new api.dom.SpanEl("drag-handle");
            dragHandle.setHtml(":::");
            nodes.push(dragHandle.getHTMLElement());

            nodes.push(document.createTextNode(label));

            if (occurrences.required()) {
                nodes.push(document.createTextNode(" "));
                var requiredMarker = new api.dom.SpanEl("required");
                nodes.push(requiredMarker.getHTMLElement());
            }
            nodes.push(document.createTextNode(":"));
            this.getEl().appendChildren(nodes);
        }
    }
}