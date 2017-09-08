module api.ui.dialog {

    import i18n = api.util.i18n;

    export class ConfirmationDialog extends ModalDialog {

        private questionEl: api.dom.H6El;
        private yesCallback: () => void;
        private noCallback: () => void;

        private yesAction: api.ui.Action;
        private noAction: api.ui.Action;

        constructor(config: ModalDialogConfig = {}) {
            super((() => {
                config.title = config.title || i18n('dialog.confirm.title');
                config.closeIconCallback = config.closeIconCallback || (() => this.close());
                return config;
            })());

            this.addClass('confirmation-dialog');

            this.questionEl = new api.dom.H6El('question');
            this.appendChildToContentPanel(this.questionEl);

            this.noAction = new api.ui.Action(i18n('action.no'), 'esc');
            this.noAction.onExecuted(() => {
                this.close();

                if (this.noCallback) {
                    this.noCallback();
                }
            });

            this.yesAction = new api.ui.Action(i18n('action.yes'));
            this.yesAction.onExecuted(() => {
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
            super.close();
            api.ui.mask.BodyMask.get().removeClass('confirmation-dialog-mask');
            this.remove();
        }
    }

}
