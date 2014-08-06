module api.ui.text {

    export interface TextEditorEditableArea {

        getElement(): api.dom.Element;

        processChanges();

    }

}