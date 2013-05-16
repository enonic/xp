module admin.ui {

    export class TopBarMenuItem {
        ext;
        private titleContainer:any; // Ext.Component
        private text1:string;
        private text2:string;

        constructor(text1:string, text2:string, card:any, tabBar:any, closable:bool, disabled:bool, editing:bool, hidden:bool,
                    iconClass:string, iconSrc:string) {
            this.text1 = text1;
            this.text2 = text2;
            var tbmi = new Ext.container.Container({
                itemId: 'topBarMenuItem',
                cls: 'admin-topbar-menu-item',
                activeCls: 'active',
                isMenuItem: true,
                canActivate: true,
                card: card,
                tabBar: tabBar,
                closable: closable,
                disabled: disabled,
                editing: editing,
                hidden: hidden,
                iconClass: iconClass,
                iconSrc: iconSrc,
                text1: text1,
                text2: text2,
                layout: {
                    type: 'hbox',
                    align: 'middle'
                }
            });
            this.ext = tbmi;

            tbmi.onClick = (e) => {
                this.onClick(e);
            };
            tbmi.activate = () => {
                console.log('activate');
                this.activate();
            };
            tbmi.deactivate = () => {
                console.log('deactivate');
                this.deactivate();
            };

            tbmi.enableBubble('closeMenuItem');
            this.initComponent(tbmi);
        }

        private initComponent(topBarMenuItem):void {
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
                var titleContainer = new Ext.Component({});
                titleContainer.flex = 1;
                titleContainer.itemId = 'titleContainer';
                titleContainer.styleHtmlContent = true;
                titleContainer.tpl = '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>';
                titleContainer.data = {
                    text1: this.text1,
                    text2: this.text2
                };
                items.push(titleContainer);
                this.titleContainer = titleContainer;
            }
            if (topBarMenuItem.closable !== false) {
                var closeButton = new Ext.Component({});
                closeButton.autoEl = 'a';
                closeButton.cls = 'close-button icon-remove icon-large';
                closeButton.margins = '0 0 0 12px';
                closeButton.on('afterrender', (cmp) => {
                    cmp.el.on('click', () => {
                        this.deactivate();
                        topBarMenuItem.fireEvent('closeMenuItem', topBarMenuItem);
                    });
                });
                items.push(closeButton);
            }

            topBarMenuItem.add(items);
            topBarMenuItem.addEvents('activate', 'deactivate', 'click', 'closeMenuItem');
        }

        private activate():void {
            console.log('activate');
            var me = this.ext;

            if (!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
                me.el.addCls(me.activeCls);
                me.focus();
                me.activated = true;
                me.fireEvent('activate', me);
            }
        }

        private deactivate():void {
            console.log('deactivate');
            var me = this.ext;

            if (me.activated) {
                me.el.removeCls(me.activeCls);
                me.blur();
                me.activated = false;
                me.fireEvent('deactivate', me);
            }
        }

        private onClick(e):bool {
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

        updateTitleContainer():void {
            this.titleContainer.update({
                text1: this.text1,
                text2: this.text2
            });
        }
    }
}
