import '../../../api.ts';

import UserStore = api.security.UserStore;
import Principal = api.security.Principal;
import UserItem = api.security.UserItem;
import i18n = api.util.i18n;

export class DeleteUserItemAction extends api.ui.Action {

    constructor(wizardPanel: api.app.wizard.WizardPanel<UserItem>) {
        super(i18n('action.delete'), 'mod+del', true);

        const confirmation = new api.ui.dialog.ConfirmationDialog()
            .setQuestion(i18n('dialog.delete.question'))
            .setNoCallback(null)
            .setYesCallback(() => {

                wizardPanel.close();

                let persistedItem = wizardPanel.getPersistedItem();
                let isPrincipal = !!persistedItem && (persistedItem instanceof Principal);
                let userItemKey;
                if (isPrincipal) {
                    userItemKey = (<Principal>persistedItem).getKey();
                    new api.security.DeletePrincipalRequest()
                        .setKeys([userItemKey])
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<any>) => {
                            let json = jsonResponse.getJson();

                            if (json.results && json.results.length > 0) {
                                let key = json.results[0].principalKey;

                                api.notify.showFeedback(i18n('notify.deleted.principal', key));
                                api.security.UserItemDeletedEvent.create().setPrincipals([<Principal>persistedItem]).build().fire();
                            }
                        });
                } else {
                    userItemKey = (<UserStore>persistedItem).getKey();
                    new api.security.DeleteUserStoreRequest()
                        .setKeys([userItemKey])
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<any>) => {
                            let json = jsonResponse.getJson();

                            if (json.results && json.results.length > 0) {
                                let key = json.results[0].userStoreKey;

                                api.notify.showFeedback(i18n('notify.deleted.userstore', key));
                                api.security.UserItemDeletedEvent.create().setUserStores([<UserStore>persistedItem]).build().fire();
                            }
                        });
                }
            });

        this.onExecuted(() => {
            confirmation.open();
        });
    }
}
