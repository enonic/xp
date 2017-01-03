import "../../api.ts";

import User = api.security.User;
import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import PrincipalLoader = api.security.PrincipalLoader;
import RoleKeys = api.security.RoleKeys;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;

import PrincipalComboBox = api.ui.security.PrincipalComboBox;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;

export class UserMembershipsWizardStepForm extends api.app.wizard.WizardStepForm {

    private groups: PrincipalComboBox;

    private roles: PrincipalComboBox;

    private principal: Principal;

    private groupsLoaded: boolean;

    private rolesLoaded: boolean;

    constructor() {
        super("user-memberships");

        this.groupsLoaded = false;
        this.rolesLoaded = false;

        let groupsLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP]);
        let rolesLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.ROLE]).skipPrincipals([RoleKeys.EVERYONE,
            RoleKeys.AUTHENTICATED]);

        this.groups = PrincipalComboBox.create().setLoader(groupsLoader).build();
        groupsLoader.load();
        this.roles = PrincipalComboBox.create().setLoader(rolesLoader).build();
        rolesLoader.load();

        let groupsHandler = () => {
            this.groupsLoaded = true;
            this.selectMembership();
            this.groups.unLoaded(groupsHandler);
        };
        let rolesHandler = () => {
            this.rolesLoaded = true;
            this.selectMembership();
            this.roles.unLoaded(rolesHandler);
        };

        this.groups.onLoaded(groupsHandler);
        this.roles.onLoaded(rolesHandler);

        let groupsFormItem = new FormItemBuilder(this.groups).setLabel('Groups').build();

        let rolesFormItem = new FormItemBuilder(this.roles).setLabel('Roles').build();

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(groupsFormItem);
        fieldSet.add(rolesFormItem);

        let form = new api.ui.form.Form().add(fieldSet);

        this.appendChild(form);

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
        this.selectMembership();
    }

    private selectMembership(): void {
        if (!!this.principal && this.groupsLoaded && this.rolesLoaded) {

            this.groups.clearSelection();
            this.roles.clearSelection();

            let groups = this.principal.asUser().getMemberships().filter((el) => {
                return el.isGroup();
            }).map((el) => {
                return el.getKey().toString();
            });

            let roles = this.principal.asUser().getMemberships().filter((el) => {
                return el.isRole();
            }).map((el) => {
                return el.getKey().toString();
            });

            this.groups.getDisplayValues().filter((principal: Principal) => {
                return groups.indexOf(principal.getKey().toString()) >= 0;
            }).forEach((selection) => {
                this.groups.select(selection);
            });

            this.roles.getDisplayValues().filter((principal: Principal) => {
                return roles.indexOf(principal.getKey().toString()) >= 0;
            }).forEach((selection) => {
                this.roles.select(selection);
            });
        }
    }

    getMemberships(): Principal[] {
        return this.groups.getSelectedDisplayValues().concat(this.roles.getSelectedDisplayValues()).map((el) => {
            return Principal.fromPrincipal(el);
        });
    }

    giveFocus(): boolean {
        return this.groups.giveFocus();
    }
}
