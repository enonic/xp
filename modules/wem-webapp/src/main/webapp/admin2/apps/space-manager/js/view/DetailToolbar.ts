module admin.ui {

    export class DetailToolbar {
        ext;

        constructor() {
            var tbar = new Ext.toolbar.Toolbar({
                itemId: 'spaceDetailToolbar',
                cls: 'admin-toolbar',
                defaults: {
                    scale: 'medium'
                }
            });
            var editButton = new Ext.button.Button({
                text: 'Edit',
                action: 'editSpace'
            });
            var deleteButton = new Ext.button.Button({
                text: 'Delete',
                action: 'deleteSpace'
            });
            var separator = new Ext.toolbar.Fill();
            var closeButton = new Ext.button.Button({
                text: 'Close',
                action: 'closeSpace'
            });
            tbar.add(editButton, deleteButton, separator, closeButton);

            this.ext = tbar;
        }
    }

}
