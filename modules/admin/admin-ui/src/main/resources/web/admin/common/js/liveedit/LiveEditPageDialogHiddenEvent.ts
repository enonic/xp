module api.liveedit {

    import ModalDialog = api.util.htmlarea.dialog.ModalDialog;

    export class LiveEditPageDialogHiddenEvent extends api.event.Event {

        private dialog: ModalDialog;

        constructor(dialog?: ModalDialog) {
            super();
            this.dialog = dialog;
        }

        getModalDialog(): ModalDialog {
            return this.dialog;
        }

        static on(handler: (event: LiveEditPageDialogHiddenEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: LiveEditPageDialogHiddenEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}