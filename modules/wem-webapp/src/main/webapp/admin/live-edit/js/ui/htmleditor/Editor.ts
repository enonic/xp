module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class Editor extends LiveEdit.ui.Base {

        toolbar:EditorToolbar;

        constructor() {
            super();

            this.toolbar = new LiveEdit.ui.EditorToolbar();
            this.registerGlobalListeners();
        }

        registerGlobalListeners():void {
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, paragraphComponent) => this.activate(paragraphComponent));
            $(window).on('leaveParagraphComponent.liveEdit', (event:JQueryEventObject, paragraphComponent) => this.deActivate(paragraphComponent));
            $(window).on('editorToolbarButtonClick.liveEdit', (event:JQueryEventObject, tag:string) => document.execCommand(tag, false, null));
        }

        activate(paragraphComponent:LiveEdit.component.Component):void {
            paragraphComponent.getElement().attr('contenteditable', true);
            paragraphComponent.getElement().get(0).focus();
        }

        deActivate(paragraphComponent:LiveEdit.component.Component):void {
            paragraphComponent.getElement().attr('contenteditable', false);
            paragraphComponent.getElement().get(0).blur();
        }

    }
}