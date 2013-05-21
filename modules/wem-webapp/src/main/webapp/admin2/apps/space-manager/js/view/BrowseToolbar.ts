module admin.ui {
    export class BrowseToolbar {
        ext;

        constructor(region?:String) {

            var tb = this.ext = new Ext.toolbar.Toolbar({
                cls: 'admin-toolbar',
                border: true,
                itemId: 'spaceBrowseToolbar',
                region: region
            });

            var newButton = new Ext.button.Button({
                text: 'New',
                action: 'newSpace',
                scale: 'medium',
                iconAlign: 'top',
                minWidth: 64
            });

            var editButton = new Ext.button.Button({
                text: 'Edit',
                disabled: true,
                action: 'editSpace',
                scale: 'medium',
                iconAlign: 'top',
                minWidth: 64,
                handler: () => {
                    new APP.event.OpenSpaceWizardEvent().fire();
                }
            });

            var openButton = new Ext.button.Button({
                text: 'Open',
                disabled: true,
                action: 'viewSpace',
                scale: 'medium',
                iconAlign: 'top',
                minWidth: 64
            });

            var deleteButton = new Ext.button.Button({
                text: 'Delete',
                disabled: true,
                action: 'deleteSpace',
                scale: 'medium',
                iconAlign: 'top',
                minWidth: 64,
                handler: () => {
                    new APP.event.DeletePromptEvent(components.gridPanel.getSelection()).fire();
                }
            });

            tb.add(newButton, editButton, openButton, deleteButton);

            APP.event.GridSelectionChangeEvent.on((event) => {
                var selected = event.getModel();
                var enable = selected && selected.length > 0;
                editButton.setDisabled(!enable);
                openButton.setDisabled(!enable);
                deleteButton.setDisabled(!enable);
            });
        }
    }
}