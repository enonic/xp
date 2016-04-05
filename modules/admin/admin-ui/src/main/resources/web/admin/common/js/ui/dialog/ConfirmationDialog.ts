module api.ui.dialog {

    export class ConfirmationDialog extends ModalDialog {

        private static instance: ConfirmationDialog;

        private questionEl: api.dom.DivEl;
        private yesCallback: () => void;
        private noCallback: () => void;

        private yesAction: api.ui.Action;
        private noAction: api.ui.Action;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Confirmation")
            });

            this.addClass("confirmation-dialog");

            this.questionEl = new api.dom.DivEl("question");
            this.appendChildToContentPanel(this.questionEl);

            this.noAction = new api.ui.Action("No", "esc");
            this.noAction.onExecuted(() => {
                this.close();
                if (this.noCallback) {
                    this.noCallback();
                }
            });

            this.yesAction = new api.ui.Action("Yes");
            this.yesAction.onExecuted(() => {
                this.close();
                if (this.yesCallback) {
                    this.yesCallback();
                }
            });

            this.addAction(this.yesAction, true);
            this.addAction(this.noAction);

            api.dom.Body.get().appendChild(this);
        }

        static get(): ConfirmationDialog {
            if (!ConfirmationDialog.instance) {
                ConfirmationDialog.instance = new ConfirmationDialog();
            }
            return ConfirmationDialog.instance;
        }

        setQuestion(question: string): ConfirmationDialog {
            this.questionEl.getEl().setInnerHtml(question);
            return this;
        }

        setYesCallback(callback: ()=>void): ConfirmationDialog {
            this.yesCallback = callback;
            return this;
        }

        setNoCallback(callback: () => void): ConfirmationDialog {
            this.noCallback = callback;
            return this;
        }
    }

}