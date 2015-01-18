module api.ui.security.acl {

    import Permission = api.security.acl.Permission;
    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class AccessControlListView extends api.ui.selector.list.ListBox<AccessControlEntry> {

        private itemValueChangedListeners: {(item: AccessControlEntry): void}[] = [];
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
            itemView.onValueChanged((item: AccessControlEntry) => {
                this.notifyItemValueChanged(item);
            });
            return itemView;
        }

        getItemId(item: AccessControlEntry): string {
            return item.getPrincipalKey().toString();
        }

        onItemValueChanged(listener: (item: AccessControlEntry) => void) {
            this.itemValueChangedListeners.push(listener);
        }

        unItemValueChanged(listener: (item: AccessControlEntry) => void) {
            this.itemValueChangedListeners = this.itemValueChangedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyItemValueChanged(item: AccessControlEntry) {
            this.itemValueChangedListeners.forEach((listener) => {
                listener(item);
            })
        }

        setItemsEditable(editable: boolean): AccessControlListView {
            if (this.itemsEditable != editable) {
                this.itemsEditable = editable;
                this.refreshList();
            }
            return this;
        }

        isItemsEditable(): boolean {
            return this.itemsEditable;
        }

    }

}