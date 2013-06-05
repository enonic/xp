module app_ui {

    export class AdminImageButton {
        ext;
        private popupTpl:string;
        private popupData:Object;
        private popupPanel:any; // Ext.panel.Panel

        constructor(iconUrl:string, popupTpl:string, popupData:Object) {
            this.popupTpl = popupTpl;
            this.popupData = popupData;
            var button = new Ext.button.Button({
                itemId: 'adminImageButton',
                cls: 'admin-image-button',
                scale: 'large',
                icon: iconUrl
            });
            button.on('click', this.onClick, this);
            this.ext = button;
        }

        private onClick(button:any) {
            if (!this.popupPanel) {
                this.popupPanel = new Ext.panel.Panel({
                    floating: true,
                    cls: 'admin-toolbar-popup',
                    border: false,
                    tpl: this.popupTpl,
                    data: this.popupData,
                    styleHtmlContent: true,
                    renderTo: Ext.getBody(),
                    listeners: {
                        afterrender: function (cont) {
                            cont.show();
                            cont.setPagePosition(cont.el.getAlignToXY(button.el, "tr-br?"));
                        }
                    }
                });
            } else {
                if (this.popupPanel.isHidden()) {
                    this.popupPanel.show();
                } else {
                    this.popupPanel.hide();
                }
            }
        }
    }
}
