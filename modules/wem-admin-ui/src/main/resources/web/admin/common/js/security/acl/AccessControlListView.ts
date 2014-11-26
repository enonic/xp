module api.security.acl {

    import Permission = api.security.acl.Permission;
    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;

    export class AccessControlListView extends api.ui.selector.list.ListBox<AccessControlEntry> {

        private itemsEditable: boolean = true;

        constructor(className?: string) {
            super('access-control-list' + (className ? " " + className : ""));
        }

        createItemView(entry: AccessControlEntry): AccessControlEntryView {
            var itemView = new AccessControlEntryView(entry);
            itemView.setEditable(this.itemsEditable);
            itemView.onRemoveClicked(() => {
                this.removeItem(entry);
            });
            return itemView;
        }

        getItemId(item: AccessControlEntry): string {
            return item.getPrincipalKey().toString();
        }

        setItemsEditable(editable: boolean): AccessControlListView {
            if (this.itemsEditable != editable) {
                this.refreshList();
                this.itemsEditable = editable;
            }
            return this;
        }

        isItemsEditable(): boolean {
            return this.itemsEditable;
        }

    }

}