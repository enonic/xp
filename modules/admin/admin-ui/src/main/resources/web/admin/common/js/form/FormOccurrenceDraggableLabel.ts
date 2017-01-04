module api.form {

    export class FormOccurrenceDraggableLabel extends api.dom.DivEl {

        constructor(label: string, occurrences: Occurrences, note?: string) {
            super("form-occurrence-draggable-label");

            let nodes: Node[] = [];

            let dragHandle = new api.dom.SpanEl("drag-handle");
            dragHandle.setHtml(":::");
            nodes.push(dragHandle.getHTMLElement());

            nodes.push(document.createTextNode(label));

            if (!!note) {
                let noteEl = new api.dom.Element(new api.dom.NewElementBuilder().setTagName('sup').setGenerateId(true));
                noteEl.addClass("note");
                noteEl.setHtml(note);
                nodes.push(noteEl.getHTMLElement());
            }

            if (occurrences.required()) {
                nodes.push(document.createTextNode(" "));
                let requiredMarker = new api.dom.SpanEl("required");
                nodes.push(requiredMarker.getHTMLElement());
            }
            nodes.push(document.createTextNode(":"));
            this.getEl().appendChildren(nodes);
        }
    }
}