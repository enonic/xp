module LiveEdit.ui {
    var $ = $liveedit;

    export class Editor extends LiveEdit.ui.Base {

        toolbar:EditorToolbar;

        constructor() {
            super();

            this.toolbar = new LiveEdit.ui.EditorToolbar();
            this.registerGlobalListeners();

            console.log('Editor instantiated. Using jQuery ' + $().jquery);
        }

        registerGlobalListeners():void {
            $(window).on('component.onParagraphEdit', (event:JQueryEventObject, paragraph:JQuery) => {
                this.activate(paragraph);
            });

            $(window).on('component.onParagraphEditLeave', (event:JQueryEventObject, paragraph:JQuery) => {
                this.deActivate(paragraph);
            });

            $(window).on('editorToolbar.onButtonClick', (event:JQueryEventObject, tag:string) => {
                // Simplest implementation for now.
                document.execCommand(tag, false, null);
            });
        }

        activate(paragraph:JQuery):void {
            paragraph.get(0).contentEditable = true;
            paragraph.get(0).focus();
        }

        deActivate(paragraph:JQuery):void {
            paragraph.get(0).contentEditable = false;
            paragraph.get(0).blur();
        }

    }
}