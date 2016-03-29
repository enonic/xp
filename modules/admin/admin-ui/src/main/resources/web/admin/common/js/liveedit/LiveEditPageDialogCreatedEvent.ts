module api.liveedit {

    import ModalDialog = api.util.htmlarea.dialog.ModalDialog;

    export class LiveEditPageDialogCreatedEvent extends api.event.Event {

        private dialog: ModalDialog;

        private config: any;

        constructor(dialog: ModalDialog, config: any) {
            super();
            this.dialog = dialog;
            this.config = config;
        }

        getModalDialog(): ModalDialog {
            return this.dialog;
        }

        getConfig(): any {
            return this.config;
        }

        static on(handler: (event: LiveEditPageDialogCreatedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: LiveEditPageDialogCreatedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}