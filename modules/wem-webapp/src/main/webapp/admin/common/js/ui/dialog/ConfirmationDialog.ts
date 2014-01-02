module api.ui.dialog {

    export class ConfirmationDialog extends ModalDialog {

        private static instance:ConfirmationDialog = new ConfirmationDialog();

        private questionEl:api.dom.DivEl;
        private yesCallback: () => void;
        private noCallback: () => void;

        private yesAction:api.ui.Action;
        private noAction:api.ui.Action;

        constructor() {
            super({
                title: "Confirmation",
                width: 500,
                height: 170
            });

            this.addClass("confirmation-dialog");

            this.questionEl = new api.dom.DivEl(null, "question");
            this.appendChildToContentPanel(this.questionEl);

            this.noAction = new api.ui.Action("No", "esc");
            this.noAction.addExecutionListener((action:api.ui.Action) => {
                this.close();
                if (this.noCallback) {
                    this.noCallback();
                }
            });
            this.addAction(this.noAction);

            this.yesAction = new api.ui.Action("Yes", "enter");
            this.yesAction.addExecutionListener((action:api.ui.Action) => {
                this.close();
                if (this.yesCallback) {
                    this.yesCallback();
                }
            });
            this.addAction(this.yesAction);

            api.dom.Body.get().appendChild(this);
        }

        static get():ConfirmationDialog {
            return ConfirmationDialog.instance;
        }

        setQuestion(question:string):ConfirmationDialog {
            this.questionEl.getEl().setInnerHtml(question);
            return this;
        }

        setYesCallback(callback:()=>void):ConfirmationDialog {
            this.yesCallback = callback;
            return this;
        }

        setNoCallback(callback:() => void):ConfirmationDialog {
            this.noCallback = callback;
            return this;
        }
    }

}