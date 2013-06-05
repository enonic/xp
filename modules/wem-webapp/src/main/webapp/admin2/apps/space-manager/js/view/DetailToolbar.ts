module app_ui {

    export class DetailToolbar {
        ext:Ext_toolbar_Toolbar;

        constructor() {
            var tbar = new Ext.toolbar.Toolbar({
                itemId: 'spaceDetailToolbar',
                cls: 'admin-toolbar'
            });

            var defaults = {
                scale: 'medium'
            };
            var editButton = new Ext.button.Button(Ext.apply({
                text: 'Edit',
                action: 'editSpace'
            }, defaults));
            var deleteButton = new Ext.button.Button(Ext.apply({
                text: 'Delete',
                action: 'deleteSpace'
            }, defaults));
            var separator = new Ext.toolbar.Fill();
            var closeButton = new Ext.button.Button(Ext.apply({
                text: 'Close',
                action: 'closeSpace',
                scale: 'medium'
            }, defaults));
            tbar.add(editButton, deleteButton, separator, closeButton);

            this.ext = <Ext_toolbar_Toolbar> tbar;
        }
    }

}
