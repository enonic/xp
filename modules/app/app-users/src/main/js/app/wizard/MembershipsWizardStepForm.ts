import '../../api.ts';

import User = api.security.User;
import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import PrincipalLoader = api.security.PrincipalLoader;
import RoleKeys = api.security.RoleKeys;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;
import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import FormItem = api.ui.form.FormItem;
import Fieldset = api.ui.form.Fieldset;
import LabelEl = api.dom.LabelEl;
import DivEl = api.dom.DivEl;
import i18n = api.util.i18n;

export enum MembershipsType {
    GROUPS,
    ROLES,
    ALL
}

export class MembershipsWizardStepForm extends api.app.wizard.WizardStepForm {

    private groups: PrincipalComboBox;

    private roles: PrincipalComboBox;

    private principal: Principal;

    private groupsLoaded: boolean;

    private rolesLoaded: boolean;

    private type: MembershipsType;

    constructor(type: MembershipsType) {
        super('user-memberships');

        this.type = type;

        const fieldSet = new api.ui.form.Fieldset();

        if (type !== MembershipsType.ROLES) {
            this.initGroups(fieldSet);
        }
        if (type !== MembershipsType.GROUPS) {
            this.initRoles(fieldSet);
        }

        const form = new api.ui.form.Form().add(fieldSet);

        this.appendChild(form);

        form.onFocus((event) => {
            this.notifyFocused(event);
        });
        form.onBlur((event) => {
            this.notifyBlurred(event);
        });

        this.appendChild(form);
    }

    private initGroups(fieldSet: Fieldset) {
        this.groupsLoaded = false;

        const groupsLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP]);

        this.groups = PrincipalComboBox.create().setLoader(groupsLoader).build();
        groupsLoader.load();

        const groupsHandler = () => {
            this.groupsLoaded = true;
            this.selectMembership();
            this.groups.unLoaded(groupsHandler);
        };

        this.groups.onLoaded(groupsHandler);

        const formItem = new FormItemBuilder(this.groups).setLabel(i18n('field.groups')).build();

        fieldSet.add(formItem);
    }

    private initRoles(fieldSet: Fieldset) {
        this.rolesLoaded = false;

        const rolesLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.ROLE]).skipPrincipals([RoleKeys.EVERYONE,
            RoleKeys.AUTHENTICATED]);

        this.roles = PrincipalComboBox.create().setLoader(rolesLoader).build();
        rolesLoader.load();

        const rolesHandler = () => {
            this.rolesLoaded = true;
            this.selectMembership();
            this.roles.unLoaded(rolesHandler);
        };

        this.roles.onLoaded(rolesHandler);

        const formItem = new FormItemBuilder(this.roles).setLabel(i18n('field.roles')).build();

        fieldSet.add(formItem);
    }

    layout(principal: Principal) {
        this.principal = principal;
        this.selectMembership();
    }

    private selectMembership(): void {
        const isGroupsReady = this.type !== MembershipsType.ROLES && this.groupsLoaded;
        const isRolesReady = this.type !== MembershipsType.GROUPS && this.rolesLoaded;

        if (this.principal && isGroupsReady) {

            this.groups.clearSelection();

            const groups = this.getMembershipsFromPrincipal().filter(el => el.isGroup()).map(el => el.getKey().toString());

            this.groups.getDisplayValues().filter((principal: Principal) => {
                return groups.indexOf(principal.getKey().toString()) >= 0;
            }).forEach((selection) => {
                this.groups.select(selection);
            });
        }

        if (this.principal && isRolesReady) {

            this.roles.clearSelection();

            const roles = this.getMembershipsFromPrincipal().filter(el => el.isRole()).map(el => el.getKey().toString());

            this.roles.getDisplayValues()
                .filter((principal: Principal) => roles.indexOf(principal.getKey().toString()) >= 0)
                .forEach(selection => this.roles.select(selection));
        }
    }

    getMembershipsFromPrincipal(): Principal[] {
        if (this.principal && this.principal.isUser()) {
            return this.principal.asUser().getMemberships();
        } else if (this.principal && this.principal.isGroup()) {
            return this.principal.asGroup().getMemberships();
        } else {
            return [];
        }
    }

    getMemberships(): Principal[] {
        const groups = this.type !== MembershipsType.ROLES ? this.groups.getSelectedDisplayValues() : [];
        const roles = this.type !== MembershipsType.GROUPS ? this.roles.getSelectedDisplayValues() : [];

        return [...groups, ...roles].map(Principal.fromPrincipal);
    }

    giveFocus(): boolean {
        return this.groups.giveFocus();
    }
}
