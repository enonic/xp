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
            $(window).on('editTextComponent.liveEdit', (event:JQueryEventObject, textComponent) => this.activate(textComponent));
            $(window).on('leaveTextComponent.liveEdit', (event:JQueryEventObject, textComponent) => this.deActivate(textComponent));
            $(window).on('editorToolbarButtonClick.liveEdit', (event:JQueryEventObject, tag:string) => document.execCommand(tag, false, null));
        }

        activate(textComponent:LiveEdit.component.Component):void {
            textComponent.getElement().attr('contenteditable', true);
            textComponent.getElement().get(0).focus();
        }

        deActivate(textComponent:LiveEdit.component.Component):void {
            textComponent.getElement().attr('contenteditable', false);
            textComponent.getElement().get(0).blur();
        }

    }
}