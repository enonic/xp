module admin.ui {

    export class DetailToolbar {
        ext;

        private isLiveMode:bool = false;

        constructor(isLiveMode?:bool) {
            this.isLiveMode = isLiveMode;

            var tbar = new Ext.toolbar.Toolbar({
                itemId: 'contentDetailToolbar',
                cls: 'admin-toolbar'
            });

            var defaults = {
                scale: 'medium'
            };

            var btnNew = this.createButton({
                text: 'New',
                action: 'newContent'
            }, defaults);

            var btnEdit = this.createButton({
                text: 'Edit',
                action: 'editContent'
            }, defaults);

            var btnDelete = this.createButton({
                text: 'Delete',
                action: 'deleteContent'
            }, defaults);

            var btnDuplicate = this.createButton({
                text: 'Duplicate',
                action: 'duplicateContent'
            }, defaults);

            var btnMove = this.createButton({
                text: 'Move',
                action: 'moveContent'
            }, defaults);

            var btnExport = this.createButton({
                text: 'Export'
            }, defaults);

            var separator = new Ext.toolbar.Fill();

            var cycle = this.createCycle();

            var toggleSlide = this.createToggleSlide();

            var btnClose = this.createButton({
                text: 'Close',
                action: 'closeContent'
            }, defaults);

            tbar.add(btnNew, btnEdit, btnDelete, btnDuplicate, btnMove, btnExport, separator, cycle, toggleSlide, btnClose);

            this.ext = tbar;
        }

        private createButton(config, defaults) {
            return new Ext.button.Button(Ext.apply(config, defaults));
        }

        private createCycle() {
            return new Ext.button.Cycle({
                itemId: 'deviceCycle',
                disabled: !this.isLiveMode,
                showText: true,
                prependText: 'Device: ',
                menu: {
                    items: [
                        {
                            text: 'Desktop',
                            checked: true,
                            device: 'DESKTOP'
                        },
                        {
                            text: 'iPhone 5 Vertical',
                            device: 'IPHONE_5_VERTICAL'
                        },
                        {
                            text: 'iPhone 5 Horizontal',
                            device: 'IPHONE_5_HORIZONTAL'
                        },
                        {
                            text: 'iPad 3 Vertical',
                            device: 'IPAD_3_VERTICAL'
                        },
                        {
                            text: 'iPad 3 Horizontal',
                            device: 'IPAD_3_HORIZONTAL'
                        }
                    ]
                }
            });
        }

        private createToggleSlide() {
            return Ext.create({
                xtype: 'toggleslide',
                onText: 'Preview',
                offText: 'Details',
                action: 'toggleLive',
                state: this.isLiveMode,
                listeners: {
                    change: function (toggle, state) {
                        this.isLiveMode = state;
                    }
                }
            });
        }
    }
}
