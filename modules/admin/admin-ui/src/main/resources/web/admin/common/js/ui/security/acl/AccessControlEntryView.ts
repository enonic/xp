module api.ui.security.acl {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import Permission = api.security.acl.Permission;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class AccessControlEntryView extends api.ui.security.PrincipalViewer {

        private ace: AccessControlEntry;

        private accessSelector: AccessSelector;
        private permissionSelector: PermissionSelector;

        private removeButton: api.dom.AEl;

        private valueChangedListeners: {(item: AccessControlEntry): void}[] = [];
        private editable: boolean = true;

        public static debug: boolean = false;

        constructor(ace: AccessControlEntry) {
            super();
            this.setClass('access-control-entry');
            //this.toggleClass('inherited', ace.isInherited());

            this.ace = ace;

            this.setAccessControlEntry(this.ace, true);
        }

        doLayout(object: Principal) {
            super.doLayout(object);

            if (AccessControlEntryView.debug) {
                console.debug("AccessControlEntryView.doLayout");
            }

            // permissions will be set on access selector value change

            if (!this.accessSelector) {
                this.accessSelector = new AccessSelector();
                this.appendChild(this.accessSelector);
            }
            this.accessSelector.setValue(AccessControlEntryView.getAccessValueFromEntry(this.ace), true);

            if (!this.removeButton) {
                this.removeButton = new api.dom.AEl("icon-close");
                this.removeButton.onClicked((event: MouseEvent) => {
                    this.notifyRemoveClicked(event);
                    event.stopPropagation();
                    event.preventDefault();
                    return false;
                });
                this.appendChild(this.removeButton);
            }

            if (!this.permissionSelector) {
                this.permissionSelector = new PermissionSelector();
                this.permissionSelector.onValueChanged((event: api.ValueChangedEvent) => {
                    this.toggleClass("dirty", event.getNewValue() != JSON.stringify({
                            allow: this.ace.getAllowedPermissions().sort(),
                            deny: this.ace.getDeniedPermissions().sort()
                        }));
                    this.notifyValueChanged(this.getAccessControlEntry());
                });

                // this.toggleClass("dirty", !ace.isInherited());

                this.accessSelector.onValueChanged((event: api.ValueChangedEvent) => {
                    if (Access[event.getNewValue()] == Access.CUSTOM) {
                        this.permissionSelector.show();
                    } else {
                        if (Access[event.getOldValue()] == Access.CUSTOM) {
                            this.permissionSelector.hide();
                        }
                        this.permissionSelector.setValue(this.getPermissionsValueFromAccess(Access[event.getNewValue()]));
                    }
                });

                if (this.accessSelector.getValue() == Access.CUSTOM) {
                    this.permissionSelector.show();
                }
                this.appendChild(this.permissionSelector);
            }
            this.permissionSelector.setValue({allow: this.ace.getAllowedPermissions(), deny: this.ace.getDeniedPermissions()}, true);
        }

        getPermissionSelector(): PermissionSelector {
            return this.permissionSelector;
        }

        getValueChangedListeners(): {(item: AccessControlEntry): void}[] {
            return this.valueChangedListeners;
        }

        setEditable(editable: boolean) {
            if (editable != this.editable) {
                this.permissionSelector.setEnabled(editable);
                this.accessSelector.setEnabled(editable);
                this.editable = editable;
            }
        }

        isEditable(): boolean {
            return this.editable;
        }

        onValueChanged(listener: (item: AccessControlEntry) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (item: AccessControlEntry) => void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        notifyValueChanged(item: AccessControlEntry) {
            this.valueChangedListeners.forEach((listener) => {
                listener(item);
            });
        }

        public setAccessControlEntry(ace: AccessControlEntry, silent?: boolean) {
            this.ace = ace;

            let principal = Principal.create().setKey(ace.getPrincipalKey()).setDisplayName(ace.getPrincipalDisplayName()).build();
            this.setObject(principal);

            this.doLayout(principal);
        }

        public getAccessControlEntry(): AccessControlEntry {
            let permissions = this.permissionSelector.getValue();
            let ace = new AccessControlEntry(this.ace.getPrincipal());
            ace.setAllowedPermissions(permissions.allow);
            ace.setDeniedPermissions(permissions.deny);
            return ace;
        }

        public static getAccessValueFromEntry(ace: AccessControlEntry): Access {

            if (ace.getDeniedPermissions().length == 0) {
                let allowedPermissions = ace.getAllowedPermissions();
                if (this.onlyFullAccess(allowedPermissions)) {
                    return Access.FULL;
                }
                if (this.canOnlyPublish(allowedPermissions)) {
                    return Access.PUBLISH;
                }
                if (this.canOnlyWrite(allowedPermissions)) {
                    return Access.WRITE;
                }
                if (this.canOnlyRead(allowedPermissions)) {
                    return Access.READ;
                }
            }
            return Access.CUSTOM;
        }

        private static canRead(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0;
        }

        private static canOnlyRead(allowed: Permission[]): boolean {
            return this.canRead(allowed) && allowed.length === 1;
        }

        private static canWrite(allowed: Permission[]): boolean {
            return this.canRead(allowed) &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0;
        }

        private static canOnlyWrite(allowed: Permission[]): boolean {
            return this.canWrite(allowed) && allowed.length === 4;
        }

        private static canPublish(allowed: Permission[]): boolean {
            return this.canWrite(allowed) &&
                   allowed.indexOf(Permission.PUBLISH) >= 0;
        }

        private static canOnlyPublish(allowed: Permission[]): boolean {
            return this.canPublish(allowed) && allowed.length === 5;
        }

        private static isFullAccess(allowed: Permission[]): boolean {
            return this.canPublish(allowed) &&
                   allowed.indexOf(Permission.READ_PERMISSIONS) >= 0 &&
                   allowed.indexOf(Permission.WRITE_PERMISSIONS) >= 0;
        }

        private static onlyFullAccess(allowed: Permission[]): boolean {
            return this.isFullAccess(allowed) && allowed.length === 7;
        }

        private getPermissionsValueFromAccess(access: Access) {
            let permissions = {
                allow: [],
                deny: []
            };
            // Falls-through are intended !
            switch (access) {
            case Access.FULL:
                permissions.allow.push(Permission.READ_PERMISSIONS);
                permissions.allow.push(Permission.WRITE_PERMISSIONS);
            case Access.PUBLISH:
                permissions.allow.push(Permission.PUBLISH);
            case Access.WRITE:
                permissions.allow.push(Permission.CREATE);
                permissions.allow.push(Permission.MODIFY);
                permissions.allow.push(Permission.DELETE);
            case Access.READ:
                permissions.allow.push(Permission.READ);
                break;
            }
            permissions.allow.sort();
            permissions.deny.sort();
            return permissions;
        }
    }

}