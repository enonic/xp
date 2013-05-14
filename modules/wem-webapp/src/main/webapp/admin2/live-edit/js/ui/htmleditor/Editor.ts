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
            $(window).on('paragraphEdit.liveEdit.component', (event:JQueryEventObject, paragraph:JQuery) => {
                this.activate(paragraph);
            });

            $(window).on('paragraphLeave.liveEdit.component', (event:JQueryEventObject, paragraph:JQuery) => {
                this.deActivate(paragraph);
            });

            $(window).on('buttonClick.liveEdit.editorToolbar', (event:JQueryEventObject, tag:string) => {
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