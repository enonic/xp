module api.ui.security.acl {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import Permission = api.security.acl.Permission;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class AccessControlListItem extends api.ui.security.PrincipalViewer {

        private ace: AccessControlEntry;

        private accessSelector: AccessSelector;
        private permissionSelector: PermissionSelector;

        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

        constructor(ace: AccessControlEntry) {
            super();
            this.setClass('access-control-list-item');

            this.ace = ace;

            this.accessSelector = new AccessSelector();
            this.appendChild(this.accessSelector);

            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));
            this.appendChild(removeButton);

            this.permissionSelector = new PermissionSelector();
            this.permissionSelector.onValueChanged((event: ValueChangedEvent) => {
                this.toggleClass("modified", event.getNewValue() != JSON.stringify({
                    allow: this.ace.getAllowedPermissions().sort(),
                    deny: this.ace.getDeniedPermissions().sort()
                }));
            });
            this.appendChild(this.permissionSelector);

            this.accessSelector.onValueChanged((event: ValueChangedEvent) => {
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

        public onValueChanged(listener: (event: api.ui.ValueChangedEvent) => void) {
            this.permissionSelector.onValueChanged(listener);
        }

        public unValueChanged(listener: (event: api.ui.ValueChangedEvent) => void) {
            this.permissionSelector.unValueChanged(listener);
        }

        public onRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners.push(listener);
        }

        public unRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners = this.removeClickedListeners.filter((current) => {
                return current !== listener;
            })
        }

        private notifyRemoveClicked(event: MouseEvent) {
            this.removeClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        public setAccessControlEntry(ace: AccessControlEntry, silent?: boolean) {
            this.ace = ace;

            var principal = new Principal(
                ace.getPrincipalKey(),
                ace.getPrincipalDisplayName(),
                ace.getPrincipalKey().getType(),
                ace.getPrincipalModifiedTime());
            this.setObject(principal);

            var permissions = {
                allow: ace.getAllowedPermissions().sort(),
                deny: ace.getDeniedPermissions().sort()
            };
            this.permissionSelector.setValue(permissions, silent);
            this.accessSelector.setValue(this.getAccessValueFromPermissions(permissions), silent);
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

        private getAccessValueFromPermissions(permissions: {allow:Permission[]; deny:Permission[]}): Access {

            if (permissions.deny.length == 0) {
                if (this.isFullAccess(permissions.allow)) {
                    return Access.FULL;
                } else if (this.isCanPublish(permissions.allow)) {
                    return Access.PUBLISH;
                } else if (this.isCanWrite(permissions.allow)) {
                    return Access.WRITE;
                } else if (this.isCanRead(permissions.allow)) {
                    return Access.READ;
                }
            }
            return Access.CUSTOM;
        }

        private isCanRead(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0;
        }

        private isCanWrite(allowed: Permission[]): boolean {
            return this.isCanRead(allowed) &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0;
        }

        private isCanPublish(allowed: Permission[]): boolean {
            return this.isCanWrite(allowed) &&
                   allowed.indexOf(Permission.PUBLISH) >= 0;
        }

        private isFullAccess(allowed: Permission[]): boolean {
            return this.isCanPublish(allowed) &&
                   allowed.indexOf(Permission.READ_PERMISSIONS) >= 0 &&
                   allowed.indexOf(Permission.WRITE_PERMISSIONS) >= 0;
        }


    }

}