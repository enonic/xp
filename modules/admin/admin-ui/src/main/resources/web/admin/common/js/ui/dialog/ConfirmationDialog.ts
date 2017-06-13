module api.ui.dialog {

    export class ConfirmationDialog extends ModalDialog {

        private questionEl: api.dom.H6El;
        private yesCallback: () => void;
        private noCallback: () => void;

        private yesAction: api.ui.Action;
        private noAction: api.ui.Action;

        constructor(config: ModalDialogConfig = {}) {
            super((() => {
                config.title = config.title || 'Confirmation';
                config.closeIconCallback = config.closeIconCallback || (() => this.closeWithoutCallback());
                return config;
            })());

            this.addClass('confirmation-dialog');

            this.questionEl = new api.dom.H6El('question');
            this.appendChildToContentPanel(this.questionEl);

            this.noAction = new api.ui.Action('No', 'esc');
            this.noAction.onExecuted(() => {
                this.close();
            });

            this.yesAction = new api.ui.Action('Yes');
            this.yesAction.onExecuted(() => {
                this.noCallback = null;
                this.close();

                if (this.yesCallback) {
                    this.yesCallback();
                }
            });

            this.addAction(this.yesAction, true);
            this.addAction(this.noAction);

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

        open() {
            api.ui.mask.BodyMask.get().addClass('confirmation-dialog-mask');
            api.dom.Body.get().appendChild(this);
            super.open();
        }

        close() {
            this.closeWithoutCallback();

            if (this.noCallback) {
                this.noCallback();
            }
        }

        private closeWithoutCallback() {
            super.close();
            api.ui.mask.BodyMask.get().removeClass('confirmation-dialog-mask');
            this.remove();
        }
    }

}
