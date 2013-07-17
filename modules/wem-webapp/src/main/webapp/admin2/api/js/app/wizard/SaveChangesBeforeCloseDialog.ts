module api_app_wizard {


    export class SaveChangesBeforeCloseDialog extends api_ui_dialog.ModalDialog {

        private yesAction = new api_ui.Action('Yes', 'mod+y');
        private noAction = new api_ui.Action('No', 'mod+n');
        private cancelAction = new api_ui.Action('Cancel', 'esc');

        constructor() {
            super({
                title: "Close wizard",
                width: 500,
                height: 180
            });

            // TODO: @alb - Page should have only one SaveChangesBeforeCloseDialog.
            api_dom.Body.get().appendChild(this);

            var message = new api_dom.H6El();
            message.getEl().setInnerHtml("There are unsaved changes, do you want do you want to save them?");
            this.appendChildToContentPanel(message);

            this.setCancelAction(this.cancelAction);
            this.addAction(this.noAction);
            this.addAction(this.yesAction);

            this.cancelAction.addExecutionListener(() => {
                this.close();
            });
        }

        getYesAction():api_ui.Action {
            return this.yesAction;
        }

        getNoAction():api_ui.Action {
            return this.noAction;
        }

        getCancelAction():api_ui.Action {
            return this.cancelAction;
        }

    }

}