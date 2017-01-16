import "../../../api.ts";
import {PrincipalWizardPanel} from "../PrincipalWizardPanel";

import UserStore = api.security.UserStore;
import Principal = api.security.Principal;

export class DeleteUserItemAction extends api.ui.Action {

    constructor(wizardPanel: api.app.wizard.WizardPanel<api.Equitable>) {
        super("Delete", "mod+del", true);
        this.onExecuted(() => {
            api.ui.dialog.ConfirmationDialog.get()
                .setQuestion("Are you sure you want to delete this item?")
                .setNoCallback(null)
                .setYesCallback(() => {

                    wizardPanel.close();

                    let persistedItem = wizardPanel.getPersistedItem();
                    let isPrincipal = (wizardPanel instanceof PrincipalWizardPanel) && !!persistedItem;
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

                                    api.notify.showFeedback('Principal [' + key + '] deleted!');
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

                                    api.notify.showFeedback('UserStore [' + key + '] deleted!');
                                    api.security.UserItemDeletedEvent.create().setUserStores([<UserStore>persistedItem]).build().fire();
                                }
                            });
                    }
                }).open();
        });
    }
}
