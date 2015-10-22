module api.ui.security.acl {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import Permission = api.security.acl.Permission;
    import Access = api.security.acl.Access;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class AccessControlEntryView extends api.ui.security.PrincipalViewer {

        private ace: AccessControlEntry;

        private accessSelector: AccessSelector;
        private permissionSelector: PermissionSelector;

        private removeButton: api.dom.AEl;

        private valueChangedListeners: {(item: AccessControlEntry): void}[] = [];
        private editable: boolean = true;

        constructor(ace: AccessControlEntry) {
            super();
            this.setClass('access-control-entry');
            //this.toggleClass('inherited', ace.isInherited());

            this.ace = ace;

            this.accessSelector = new AccessSelector();

            this.removeButton = new api.dom.AEl("icon-close");
            this.removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));

            this.permissionSelector = new PermissionSelector();
            this.permissionSelector.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.toggleClass("dirty", event.getNewValue() != JSON.stringify({
                    allow: this.ace.getAllowedPermissions().sort(),
                    deny: this.ace.getDeniedPermissions().sort()
                }));
                this.notifyValueChanged(this.getAccessControlEntry());
            });

            this.permissionSelector.setValue({allow: ace.getAllowedPermissions(), deny: ace.getDeniedPermissions()}, true);
            // this.toggleClass("dirty", !ace.isInherited());

            this.accessSelector.onValueChanged((event: api.ui.ValueChangedEvent) => {
                if (Access[event.getNewValue()] == Access.CUSTOM) {
                    this.permissionSelector.show();
                } else {
                    if (Access[event.getOldValue()] == Access.CUSTOM) {
                        this.permissionSelector.hide();
                    }
                    this.permissionSelector.setValue(this.getPermissionsValueFromAccess(Access[event.getNewValue()]));
                }
            });

            this.setAccessControlEntry(this.ace, true);
        }

        doRender() {
            super.doRender();
            this.appendChild(this.accessSelector);
            this.appendChild(this.removeButton);
            this.appendChild(this.permissionSelector);
            return true;
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
            })
        }

        notifyValueChanged(item: AccessControlEntry) {
            this.valueChangedListeners.forEach((listener) => {
                listener(item);
            })
        }

        public setAccessControlEntry(ace: AccessControlEntry, silent?: boolean) {
            this.ace = ace;

            var principal = new Principal(
                ace.getPrincipalKey(),
                ace.getPrincipalDisplayName());
            this.setObject(principal);

            this.accessSelector.setValue(ace.getAccess(), silent);
            // permissions will be set on access selector value change
        }

        public getAccessControlEntry(): AccessControlEntry {
            var permissions = this.permissionSelector.getValue();
            var ace = new AccessControlEntry(this.ace.getPrincipal());
            ace.setAllowedPermissions(permissions.allow);
            ace.setDeniedPermissions(permissions.deny);
            return ace;
        }

        private getPermissionsValueFromAccess(access: Access) {
            var permissions = {
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