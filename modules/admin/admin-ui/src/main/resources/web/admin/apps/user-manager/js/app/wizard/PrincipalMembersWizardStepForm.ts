import "../../api.ts";

import Role = api.security.Role;
import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import PrincipalLoader = api.security.PrincipalLoader;
import FormItemBuilder = api.ui.form.FormItemBuilder;

import PrincipalComboBox = api.ui.security.PrincipalComboBox;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;

export class PrincipalMembersWizardStepForm extends api.app.wizard.WizardStepForm {

    private principals: PrincipalComboBox;

    private principal: Principal;

    private loader: PrincipalLoader;

    constructor(loadedHandler?: Function) {
        super();

        loadedHandler = loadedHandler || (() => { /* empty */ });
        this.loader =
            new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER]).skipPrincipals([PrincipalKey.ofAnonymous()]);

        this.principals = PrincipalComboBox.create().setLoader(this.loader).build();
        let handler = () => {
            this.selectMembers();
            loadedHandler();
            this.principals.unLoaded(handler);
        };
        this.principals.onLoaded(handler);

        let principalsFormItem = new FormItemBuilder(this.principals).setLabel('Members').build();

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(principalsFormItem);

        let form = new api.ui.form.Form().add(fieldSet);

        form.onFocus((event) => {
            this.notifyFocused(event);
        });
        form.onBlur((event) => {
            this.notifyBlurred(event);
        });

        this.appendChild(form);

    }

    layout(principal: Principal) {
        this.principal = principal;
        this.loader.skipPrincipal(principal.getKey());
        this.selectMembers();
    }

    private selectMembers(): void {
        if (!!this.principal) {
            let principalKeys = this.getPrincipalMembers().map((key: PrincipalKey) => {
                return key.toString();
            });
            let selected = this.principals.getDisplayValues().filter((principal: Principal) => {
                return principalKeys.indexOf(principal.getKey().toString()) >= 0;
            });
            selected.forEach((selection) => {
                this.principals.select(selection);
            });
        }
    }

    getMembers(): Principal[] {
        return this.principals.getSelectedDisplayValues();
    }

    getPrincipals(): PrincipalComboBox {
        return this.principals;
    }

    getPrincipal(): Principal {
        return this.principal;
    }

    getPrincipalMembers(): PrincipalKey[] {
        throw new Error('Must be implemented by inheritors');
    }

    giveFocus(): boolean {
        return this.principals.giveFocus();
    }

    getLoader(): PrincipalLoader {
        return this.loader;
    }
}
