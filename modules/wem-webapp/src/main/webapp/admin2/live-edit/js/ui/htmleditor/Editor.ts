module LiveEdit.ui {
    var $ = $liveEdit;

    export class Editor extends LiveEdit.ui.Base {

        toolbar:EditorToolbar;

        constructor() {
            super();

            this.toolbar = new LiveEdit.ui.EditorToolbar();
            this.registerGlobalListeners();
        }

        registerGlobalListeners():void {
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, paragraph:JQuery) => this.activate(paragraph));
            $(window).on('leaveParagraphComponent.liveEdit', (event:JQueryEventObject, paragraph:JQuery) => this.deActivate(paragraph));
            $(window).on('editorToolbarButtonClick.liveEdit', (event:JQueryEventObject, tag:string) => document.execCommand(tag, false, null));
        }

        activate(paragraph:JQuery):void {
            paragraph.attr('contenteditable', true);
            paragraph.get(0).focus();
        }

        deActivate(paragraph:JQuery):void {
            paragraph.attr('contenteditable', false);
            paragraph.get(0).blur();
        }

    }
}