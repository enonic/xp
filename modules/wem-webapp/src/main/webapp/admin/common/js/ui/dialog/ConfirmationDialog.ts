module api_ui_dialog {

    export class ConfirmationDialog extends ModalDialog {

        private static instance:ConfirmationDialog = new ConfirmationDialog();

        private questionEl:api_dom.DivEl;
        private yesCallback: () => void;
        private noCallback: () => void;

        private yesAction:api_ui.Action;
        private noAction:api_ui.Action;

        constructor() {
            super({
                title: "Confirmation",
                width: 500,
                height: 170
            });

            this.addClass("confirmation-dialog");

            this.questionEl = new api_dom.DivEl(null, "question");
            this.appendChildToContentPanel(this.questionEl);

            this.noAction = new api_ui.Action("No", "esc");
            this.noAction.addExecutionListener((action:api_ui.Action) => {
                this.close();
                if (this.noCallback) {
                    this.noCallback();
                }
            });
            this.addAction(this.noAction);

            this.yesAction = new api_ui.Action("Yes", "enter");
            this.yesAction.addExecutionListener((action:api_ui.Action) => {
                this.close();
                if (this.yesCallback) {
                    this.yesCallback();
                }
            });
            this.addAction(this.yesAction);

            api_dom.Body.get().appendChild(this);
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