module admin.ui {

    export class TopBarMenuItem {
        ext;

        constructor(public text1: string, public text2: string) {
            var tbmi = new Ext.container.Container({});
            this.ext = tbmi;
            tbmi.itemId = 'topBarMenuItem';
            tbmi.addCls('admin-topbar-menu-item');
            tbmi.activeCls = 'active';
            tbmi.isMenuItem = true;
            tbmi.canActivate = true;

            var layout = new Ext.layout.container.HBox();
            layout.align = 'middle';
            tbmi.layout = layout;

            tbmi.enableBubble(['closeMenuItem']);
            this.initComponent(tbmi);
        }

        private initComponent(topBarMenuItem) {
            var items = [];
            if (topBarMenuItem.iconCls || topBarMenuItem.iconSrc) {
                var image = new Ext.Img();
                image.width = 32;
                image.height = 32;
                image.margin = '0 12px 0 0';
                image.cls = topBarMenuItem.iconCls;
                image.src = topBarMenuItem.iconSrc;
                items.push(image);
            }
            if (this.text1 || this.text2) {
                var titleContainer = new Ext.Component();
                titleContainer.flex = 1;
                titleContainer.itemId = 'titleContainer';
                titleContainer.styleHtmlContent = true;
                titleContainer.tpl = '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>';
                titleContainer.data = {
                    text1: this.text1,
                    text2: this.text2
                };
                items.push(titleContainer);
            }
            if (topBarMenuItem.closable !== false) {
                var closeButton = new Ext.Component();
                closeButton.autoEl= 'a';
                closeButton.cls= 'close-button icon-remove icon-large';
                closeButton.margins= '0 0 0 12px';
                closeButton.on('afterrender', (cmp) => {
                    cmp.el.on('click', function () {
                        this.deactivate();
                        topBarMenuItem.fireEvent('closeMenuItem', topBarMenuItem);
                    });
                });
                items.push(closeButton);
            }

            topBarMenuItem.add(items);
//            topBarMenuItem.callParent(arguments);
            topBarMenuItem.addEvents('activate', 'deactivate', 'click', 'closeMenuItem');
        }

        private activate() {
            var me = this.ext;

            if (!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
                me.el.addCls(me.activeCls);
                me.focus();
                me.activated = true;
                me.fireEvent('activate', me);
            }
        }

        private deactivate() {
            var me = this.ext;

            if (me.activated) {
                me.el.removeCls(me.activeCls);
                me.blur();
                me.activated = false;
                me.fireEvent('deactivate', me);
            }
        }

        public onClick(e) {
            var me = this.ext;

            if (!me.href) {
                e.stopEvent();
            }

            if (me.disabled) {
                return false;
            }

            Ext.callback(me.handler, me.scope || me, [me, e]);
            me.fireEvent('click', me, e);

            if (!me.hideOnClick) {
                me.focus();
            }
            // return false if the checkbox was clicked to prevent item click event
            return Ext.isEmpty(Ext.fly(e.getTarget()).findParent('.close-button'));
        }

        updateTitleContainer() {
            this.ext.down('#titleContainer').update({
                text1: this.text1,
                text2: this.text2
            });
        }
    }
}
