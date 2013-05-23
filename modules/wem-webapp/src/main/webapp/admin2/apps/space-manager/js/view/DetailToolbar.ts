module admin.ui {

    export class DetailToolbar {
        ext:Ext_toolbar_Toolbar;

        constructor() {
            var tbar = new Ext.toolbar.Toolbar({
                itemId: 'spaceDetailToolbar',
                cls: 'admin-toolbar'
            });
            var editButton = new Ext.button.Button({
                text: 'Edit',
                action: 'editSpace',
                scale: 'medium'
            });
            var deleteButton = new Ext.button.Button({
                text: 'Delete',
                action: 'deleteSpace',
                scale: 'medium'
            });
            var separator = new Ext.toolbar.Fill();
            var closeButton = new Ext.button.Button({
                text: 'Close',
                action: 'closeSpace',
                scale: 'medium'
            });
            tbar.add(editButton, deleteButton, separator, closeButton);

            this.ext = <Ext_toolbar_Toolbar> tbar;
        }
    }

}
