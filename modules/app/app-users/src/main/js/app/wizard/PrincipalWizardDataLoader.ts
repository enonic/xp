import '../../api.ts';
import {PrincipalWizardPanelParams} from './PrincipalWizardPanelParams';

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import WizardPanelParams = api.app.wizard.WizardPanelParams;

export class PrincipalWizardDataLoader {

    principal: Principal;

    loadData(params: PrincipalWizardPanelParams): wemQ.Promise<PrincipalWizardDataLoader> {

        if (!params.persistedItem && !params.principalKey) {
            return this.loadDataForNew(params);

        } else {
            return this.loadDataForEdit(params);

        }
    }

    private loadDataForNew(params: PrincipalWizardPanelParams): wemQ.Promise<PrincipalWizardDataLoader> {

        return wemQ(this);
    }

    private loadDataForEdit(params: PrincipalWizardPanelParams): wemQ.Promise<PrincipalWizardDataLoader> {

        return this.loadDataForNew(params).then((loader) => {

            return this.loadPrincipalToEdit(params).then((loadedPrincipalToEdit: Principal) => {

                this.principal = loadedPrincipalToEdit;

                return this;
            });
        });
    }

    private loadPrincipalToEdit(params: PrincipalWizardPanelParams): wemQ.Promise<Principal> {
        if (!params.persistedItem && !!params.principalKey) {
            return new api.security.GetPrincipalByKeyRequest(params.principalKey).setIncludeMemberships(true).sendAndParse();
        } else {
            return wemQ(params.persistedItem);
        }

    }

}
