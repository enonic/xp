module admin.ui {

    export class TopBarMenu {
        ext;

        private tabPanel:any;
        private activeTab:any;

        private maxWas:number;
        private nonClosableItems:any; // Ext.container.Container
        private editTitle:any; // Ext.Component
        private editItems:any; // Ext.container.Container
        private viewTitle:any; // Ext.Component
        private viewItems:any; // Ext.container.Container
        private emptyTitle:any; // Ext.Component

        constructor(tabPanel:any) {
            var tbm = new Ext.menu.Menu({});
            this.ext = tbm;
            this.tabPanel = tabPanel;
            tbm.itemId = 'topBarMenu';
            tbm.addCls('admin-topbar-menu');

            tbm.showSeparator = false;
            tbm.styleHtmlContent = true;
            tbm.overflowY = 'auto';
            tbm.overflowX = 'hidden';
            tbm.width = 300;

            var layout = new Ext.layout.container.VBox();
            layout.align = 'stretchmax';
            tbm.layout = layout;

            this.nonClosableItems = this.createNonClosableItems()
            this.editTitle = this.createEditTitle();
            this.editItems = this.createEditItems();
            this.viewTitle = this.createViewTitle();
            this.viewItems = this.createViewItems();
            this.emptyTitle = this.createEmptyTitle();

            tbm.add(this.nonClosableItems);
            tbm.add(this.editTitle);
            tbm.add(this.editItems);
            tbm.add(this.viewTitle);
            tbm.add(this.viewItems);
            tbm.add(this.emptyTitle);

            tbm.onShow = this.onShow;
            tbm.onBoxReady = this.onBoxReady;
            tbm.show = () => {
                this.show();
            };
            tbm.hide = () => {
                this.hide();
            };
            tbm.setVerticalPosition = () => {
                this.setVerticalPosition();
            };

            tbm.scrollState = { left: 0, top: 0 };
            tbm.on('closeMenuItem', this.onCloseMenuItem);
            tbm.on('resize', this.updatePosition);
        }

        private createNonClosableItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({});
            item.itemId = 'nonClosableItems';
            item.defaultType = 'topBarMenuItem';
            return item;
        }

        private createEditTitle():any /* Ext.Component */ {
            var item = new Ext.Component({});
            item.addCls('title');
            item.itemId = 'editTitle';
            item.hide();
            item.html = '<span>Editing</span>';
            return item;
        }

        private createEditItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({});
            item.itemId = 'editItems';
            item.defaultType = 'topBarMenuItem';
            return item;
        }

        private createViewTitle():any /* Ext.Component */ {
            var item = new Ext.Component({});
            item.addCls('title');
            item.itemId = 'viewTitle';
            item.hide();
            item.html = '<span>Viewing</span>';
            return item;
        }

        private createViewItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({});
            item.itemId = 'viewItems';
            item.defaultType = 'topBarMenuItem';
            return item;
        }

        private createEmptyTitle():any /* Ext.Component */ {
            var item = new Ext.Component({});
            item.addCls('info');
            item.itemId = 'emptyTitle';
            item.html = 'List is empty';
            return item;
        }

        onClick(e):void {
            var me = this.ext,
                item;

            if (me.disabled) {
                e.stopEvent();
                return;
            }

            item = (e.type === 'click') ? me.getItemFromEvent(e) : me.activeItem;
            if (item && item.isMenuItem && item.onClick(e) !== false) {
                if (me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                    this.tabPanel.setActiveTab(item.card);
                }
                this.hide();
            }
        }

        onShow():void {
            this.ext.callParent(arguments);

            if (this.activeTab) {
                this.markActiveTab(this.activeTab);
            }
        }

        onBoxReady():void {
            var tip = Ext.DomHelper.append(this.ext.el, {
                tag: 'div',
                cls: 'balloon-tip'
            }, true);
            this.ext.callParent(arguments);
        }

        onCloseMenuItem(item):void {
            if (this.tabPanel) {
                this.tabPanel.remove(item.card);
            }
            // hide menu if all closable items have been closed
            if (this.getAllItems(false).length === 0) {
                this.hide();
            }
        }

        markActiveTab(item):void {
            var me = this.ext;
            var menuItem;

            if (me.isVisible()) {

                // deactivate
                menuItem = me.el.down('.current-tab');
                if (menuItem) {
                    menuItem.removeCls('current-tab')
                }

                // activate
                if (item) {
                    menuItem = me.down('#' + item.id);
                    if (menuItem && menuItem.el) {
                        menuItem.el.addCls('current-tab')
                    }
                }

            }

            this.activeTab = item;
        }

        getItemFromEvent(e):any /* Ext.Component */ {
            var item = this.ext;
            do {
                item = item.getChildByElement(e.getTarget());
            }
            while (item && Ext.isDefined(item.getChildByElement) && item.getXType() !== 'topBarMenuItem');
            return item;
        }

        getAllItems(includeNonClosable):any[] /* Ext.Component[] */ {
            var items = [];
            if (includeNonClosable === false) {
                items = items.concat(this.editItems.query('topBarMenuItem'));
                items = items.concat(this.viewItems.query('topBarMenuItem'))
            } else {
                items = items.concat(this.ext.query('topBarMenuItem'));
            }
            return items;
        }

        addItems(items):any[] /* Ext.Component[] */ {
            if (Ext.isEmpty(items)) {
                return [];
            } else if (Ext.isObject(items)) {
                items = [].concat(items);
            }

            this.saveScrollState();

            var editItems = [];
            var viewItems = [];
            var nonClosableItems = [];
            Ext.Array.each(items, (item) => {
                if (item.closable === false) {
                    nonClosableItems.push(item);
                } else if (item.editing) {
                    editItems.push(item);
                } else {
                    viewItems.push(item);
                }
            });
            var added = [];
            if (nonClosableItems.length > 0) {
                added = added.concat(this.nonClosableItems.add(nonClosableItems));
            }
            if (editItems.length > 0) {
                var editItemObjects = [];
                Ext.Array.each(editItems, (editItem) => {
                    // defaultType: 'topBarMenuItem'
                    if (!editItem.xtype) {
                        var tbmi = new admin.ui.TopBarMenuItem(editItem.text1, editItem.text2).ext;
                        editItemObjects.push(tbmi);
                    } else {
                        editItemObjects.push(editItem);
                    }
                });

                added = added.concat(this.editItems.add(editItemObjects));
            }
            if (viewItems.length > 0) {
                var viewItemObjects = [];
                Ext.Array.each(viewItems, (viewItem) => {
                    // defaultType: 'topBarMenuItem'
                    if (!viewItem.xtype) {
                        var tbmi = new admin.ui.TopBarMenuItem(viewItem.text1, viewItem.text2).ext;
                        viewItemObjects.push(tbmi);
                    } else {
                        viewItemObjects.push(viewItem);
                    }
                });

                added = added.concat(this.viewItems.add(viewItemObjects));
            }
            this.updateTitles();

            this.restoreScrollState();

            return added;
        }

        removeAllItems(includeNonClosable):any[] /* Ext.Component[] */ {
            var me = this.ext;
            var editItems = this.editItems;
            var viewItems = this.viewItems;
            var removed = [];
            Ext.Array.each(editItems.items.items, (item) => {
                if (item && item.closable !== false) {
                    removed.push(editItems.remove(item));
                }
            });
            Ext.Array.each(viewItems.items.items, (item) => {
                if (item && item.closable !== false) {
                    removed.push(viewItems.remove(item));
                }
            });
            if (includeNonClosable) {
                var nonClosableItems = this.nonClosableItems;
                Ext.Array.each(nonClosableItems.items.items, (item) => {
                    if (item && item.closable !== false) {
                        removed.push(nonClosableItems.remove(item));
                    }
                });
            }
            this.updateTitles();
            return removed;
        }

        removeItems(items):any[] /* Ext.Component[] */ {
            if (Ext.isEmpty(items)) {
                return null;
            } else if (Ext.isObject(items)) {
                items = [].concat(items);
            }

            this.saveScrollState();

            var me = this.ext;
            var editItems = this.editItems;
            var viewItems = this.viewItems;
            var nonClosableItems = this.nonClosableItems;
            var removed = [];

            Ext.Array.each(items, (item) => {
                if (item && item.closable !== false) {
                    removed.push(editItems.remove(item));
                    removed.push(viewItems.remove(item));
                    removed.push(nonClosableItems.remove(item));
                }
            });

            this.updateTitles();

            this.restoreScrollState();
            return removed;
        }

        updateTitles():void {
            var me = this.ext;
            var editCount = this.editItems.items.getCount();
            var viewCount = this.viewItems.items.getCount();
            var nonClosableCount = this.nonClosableItems.items.getCount();
            if (editCount > 0) {
                this.editTitle.show();
            } else {
                this.editTitle.hide();
            }
            if (viewCount > 0) {
                this.viewTitle.show();
            } else {
                this.viewTitle.hide();
            }
            if ((viewCount || editCount || nonClosableCount) > 0) {
                this.emptyTitle.hide();
            } else {
                this.emptyTitle.show();
            }
        }

        // Need in case of resize while center positioned
        updatePosition(menu, width, height, oldWidth, oldHeight, opts) {
            this.ext.el.move('r', ((oldWidth - width) / 2), false);
        }

        show():any /* Ext.Component */ {
            var me = this.ext,
                parentEl, viewHeight;

            this.maxWas = me.maxHeight;

            // we need to get scope parent for height constraint
            if (!me.rendered) {
                me.doAutoRender();
            }

            // constrain the height to the current viewable area
            if (me.floating) {
                //if our reset css is scoped, there will be a x-reset wrapper on this menu which we need to skip
                parentEl = Ext.fly(me.el.getScopeParent());
                viewHeight = parentEl.getViewSize().height;
                me.maxHeight = Math.min(this.maxWas || viewHeight - 50, viewHeight - 50);
            }

            this.ext.callParent(arguments);
            return me;
        }

        hide():void {
            this.ext.callParent();

            //return back original value to calculate new height on show
            this.ext.maxHeight = this.maxWas;
        }

        setVerticalPosition():void {
            // disable position change as we adjust menu height
        }

        saveScrollState():void {
            var me = this.ext;
            if (me.rendered && !me.hidden) {
                var dom = me.body.dom,
                    state = me.scrollState;

                state.left = dom.scrollLeft;
                state.top = dom.scrollTop;
            }
        }

        restoreScrollState():void {
            var me = this.ext;
            if (me.rendered && !me.hidden) {
                var dom = me.body.dom,
                    state = me.scrollState;

                dom.scrollLeft = state.left;
                dom.scrollTop = state.top;
            }
        }
    }


}
