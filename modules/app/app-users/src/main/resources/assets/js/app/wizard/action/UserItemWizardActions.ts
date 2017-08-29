import '../../../api.ts';
import {UserItemWizardPanel} from '../UserItemWizardPanel';
import {DeleteUserItemAction} from './DeleteUserItemAction';
import UserItem = api.security.UserItem;

export class UserItemWizardActions<USER_ITEM_TYPE extends UserItem> extends api.app.wizard.WizardActions<USER_ITEM_TYPE> {

    private save: api.ui.Action;

    private close: api.ui.Action;

    private delete: api.ui.Action;

    constructor(wizardPanel: UserItemWizardPanel<USER_ITEM_TYPE>) {
        super();

        this.save = new api.app.wizard.SaveAction(wizardPanel);
        this.delete = new DeleteUserItemAction(wizardPanel);
        this.close = new api.app.wizard.CloseAction(wizardPanel);

        this.setActions(this.save, this.delete, this.close);
    }

    enableActionsForNew() {
        this.save.setEnabled(false);
        this.delete.setEnabled(false);
    }

    enableActionsForExisting() {
        this.save.setEnabled(true);
        this.delete.setEnabled(true);
    }

    getDeleteAction(): api.ui.Action {
        return this.delete;
    }

    getSaveAction(): api.ui.Action {
        return this.save;
    }

    getCloseAction(): api.ui.Action {
        return this.close;
    }

}
