module admin.ui {
    export class BrowseToolbar {
        private toolbar;

        constructor(region?:String) {

            var tb = this.toolbar = new Ext.toolbar.Toolbar();
            tb.cls = 'admin-toolbar';
            tb.border = true;
            if (region) {
                tb.region = region;
            }

            var newButton = new Ext.button.Button();
            newButton.text = 'New';
            newButton.action = 'newSpace';
            newButton.scale = 'medium';
            newButton.iconAlign = 'top';
            newButton.minWidth = 64;

            tb.add(newButton);

            var editButton = new Ext.button.Button();
            editButton.text = 'Edit';
            editButton.disabled = true;
            editButton.action = 'editSpace';
            editButton.scale = 'medium';
            editButton.iconAlign = 'top';
            editButton.minWidth = 64;

            tb.add(editButton);

            var openButton = new Ext.button.Button();
            openButton.text = 'Open';
            openButton.disabled = true;
            openButton.action = 'viewSpace';
            openButton.scale = 'medium';
            openButton.iconAlign = 'top';
            openButton.minWidth = 64;

            tb.add(openButton);

            var deleteButton = new Ext.button.Button();
            deleteButton.text = 'Delete';
            deleteButton.disabled = true;
            deleteButton.action = 'deleteSpace';
            deleteButton.scale = 'medium';
            deleteButton.iconAlign = 'top';
            deleteButton.minWidth = 64;

            tb.add(deleteButton);

            return this.toolbar;
        }

        getToolbar() {
            return this.toolbar;
        }
    }
}