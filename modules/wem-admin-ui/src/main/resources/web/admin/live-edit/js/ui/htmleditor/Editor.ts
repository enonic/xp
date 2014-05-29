module LiveEdit.ui {

    import TextView = api.liveedit.text.TextView;

    export class Editor extends LiveEdit.ui.Base {

        toolbar: EditorToolbar;

        constructor() {
            super();

            this.toolbar = new LiveEdit.ui.EditorToolbar();
            this.registerGlobalListeners();
        }

        registerGlobalListeners(): void {
            wemjq(window).on('editTextComponent.liveEdit', (event: JQueryEventObject, textComponent?) => this.activate(textComponent));
            wemjq(window).on('leaveTextComponent.liveEdit', (event: JQueryEventObject, textComponent?) => this.deActivate(textComponent));
            wemjq(window).on('editorToolbarButtonClick.liveEdit',
                (event: JQueryEventObject, tag?: string) => document.execCommand(tag, false, null));
        }

        activate(textComponent: TextView): void {
            textComponent.getElement().attr('contenteditable', true);
            textComponent.getElement().get(0).focus();
        }

        deActivate(textComponent: TextView): void {
            textComponent.getElement().attr('contenteditable', false);
            textComponent.getElement().get(0).blur();
        }

    }
}