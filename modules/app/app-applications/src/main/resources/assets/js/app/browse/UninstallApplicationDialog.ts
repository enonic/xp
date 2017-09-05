import '../../api.ts';
import {UninstallApplicationEvent} from './UninstallApplicationEvent';
import Application = api.application.Application;
import Action = api.ui.Action;
import i18n = api.util.i18n;

export class UninstallApplicationDialog extends api.ui.dialog.ModalDialog {

    private applications: Application[];

    private yesAction: Action = new Action(i18n('action.yes'));

    private noAction: Action = new Action(i18n('action.no'));

    constructor(applications: Application[]) {
        super(i18n('dialog.uninstall'));

        this.applications = applications;
        this.addClass('uninstall-dialog');

        let message = new api.dom.H6El();
        message.getEl().setInnerHtml(i18n('dialog.uninstall.question'));
        this.appendChildToContentPanel(message);

        this.yesAction.onExecuted(() => {
            new UninstallApplicationEvent(this.applications).fire();
            this.close();
        });
        this.addAction(this.yesAction);

        this.noAction.onExecuted(() => {
            this.close();
        });
        this.addAction(this.noAction);
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        super.close();
        this.remove();
    }
}
