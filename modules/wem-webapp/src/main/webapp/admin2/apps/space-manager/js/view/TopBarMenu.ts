module app_ui {

    interface Ext_TopBarMenu_Item {
        isMenuItem: bool;
        xtype: string;
        text1: string;
        text2: string;
        card: Object;
        tabBar: Object;
        closable: bool;
        disabled: bool;
        editing: bool;
        hidden: bool;
        iconClass: string;
        iconSrc: string;
    }

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
            var tbm = new Ext.menu.Menu({
                itemId: 'topBarMenu',
                cls: 'admin-topbar-menu',
                showSeparator: false,
                styleHtmlContent: true,
                overflowY: 'auto',
                overflowX: 'hidden',
                width: 300,
                layout: {
                    type: 'vbox',
                    align: 'stretchmax'
                }
            });
            this.ext = tbm;
            this.tabPanel = tabPanel;

            this.nonClosableItems = this.createNonClosableItems();
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

            Ext.Function.interceptAfter(tbm, 'onShow', this.onShow, this);

            Ext.Function.interceptAfter(tbm, 'onBoxReady', this.onBoxReady, this);

            Ext.Function.interceptAfter(tbm, 'show', this.show, this);

            Ext.Function.interceptBefore(tbm, 'hide', this.hide, this);

            Ext.override(tbm, {
                scrollState: { left: 0, top: 0 },

                onClick: (e) => {
                    return this.onClick(e);
                },
                setVerticalPosition: () => {
                    this.setVerticalPosition();
                }
            });

            tbm.on('closeMenuItem', this.onCloseMenuItem, this);
            tbm.on('resize', this.updatePosition, this);
        }

        private createNonClosableItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({
                itemId: 'nonClosableItems'
            });
            return item;
        }

        private createEditTitle():any /* Ext.Component */ {
            var item = new Ext.Component({
                cls: 'title',
                itemId: 'editTitle',
                hidden: true,
                html: '<span>Editing</span>'
            });

            return item;
        }

        private createEditItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({
                itemId: 'editItems'
            });
            return item;
        }

        private createViewTitle():any /* Ext.Component */ {
            var item = new Ext.Component({
                cls: 'title',
                itemId: 'viewTitle',
                hidden: true,
                html: '<span>Viewing</span>'
            });
            return item;
        }

        private createViewItems():any /* Ext.container.Container */ {
            var item = new Ext.container.Container({
                itemId: 'viewItems'
            });
            return item;
        }

        private createEmptyTitle():any /* Ext.Component */ {
            var item = new Ext.Component({
                cls: 'info',
                itemId: 'emptyTitle',
                html: 'List is empty'
            });
            return item;
        }

        onClick(e):void {
            var me = this.ext,
                item;

            if (me.disabled) {
                e.stopEvent();
                return;
            }

            item = (e.type === 'click') ? this.getItemFromEvent(e) : me.activeItem;
            if (item && item.isMenuItem && item.onClick(e) !== false) {
                if (me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                    this.tabPanel.setActiveTab(item.card);
                }
                me.hide();
            }
        }

        onShow():void {
            if (this.activeTab) {
                this.markActiveTab(this.activeTab);
            }
        }

        onBoxReady():void {
            var tip = Ext.DomHelper.append(this.ext.el, {
                tag: 'div',
                cls: 'balloon-tip'
            }, true);
        }

        onCloseMenuItem(item):void {
            if (this.tabPanel) {
                this.tabPanel.remove(item.card);
            }
            // hide menu if all closable items have been closed
            if (this.getAllItems(false).length === 0) {
                this.ext.hide();
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
                    menuItem = item; //me.down('#' + item.id);
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
            while (item && Ext.isDefined(item.getChildByElement) && item.isMenuItem !== true);
            return item;
        }

        getAllItems(includeNonClosable):any[] /* Ext.Component[] */ {
            var items = [];
            if (includeNonClosable === false) {
                items = items.concat(this.editItems.query('*[isMenuItem=true]'));
                items = items.concat(this.viewItems.query('*[isMenuItem=true]'))
            } else {
                items = items.concat(this.ext.query('*[isMenuItem=true]'));
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
            Ext.Array.each(items, (item:Ext_TopBarMenu_Item) => {
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
                Ext.Array.each(editItems, (editItem:Ext_TopBarMenu_Item) => {
                    // defaultType: 'topBarMenuItem'
                    if (!editItem.xtype) {
                        var tbmi = new app_ui.TopBarMenuItem(editItem.text1, editItem.text2, editItem.card, editItem.tabBar,
                            editItem.closable, editItem.disabled, editItem.editing, editItem.hidden,
                            editItem.iconClass, editItem.iconSrc).ext;
                        editItemObjects.push(tbmi);
                    } else {
                        editItemObjects.push(editItem);
                    }
                });

                added = added.concat(this.editItems.add(editItemObjects));
            }
            if (viewItems.length > 0) {
                var viewItemObjects = [];
                Ext.Array.each(viewItems, (viewItem:Ext_TopBarMenu_Item) => {
                    // defaultType: 'topBarMenuItem'
                    if (!viewItem.xtype) {
                        var tbmi = new app_ui.TopBarMenuItem(viewItem.text1, viewItem.text2, viewItem.card, viewItem.tabBar,
                            viewItem.closable, viewItem.disabled, viewItem.editing, viewItem.hidden,
                            viewItem.iconClass, viewItem.iconSrc).ext;
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
            Ext.Array.each(editItems.items.items, (item:Ext_TopBarMenu_Item) => {
                if (item && item.closable !== false) {
                    removed.push(editItems.remove(item));
                }
            });
            Ext.Array.each(viewItems.items.items, (item:Ext_TopBarMenu_Item) => {
                if (item && item.closable !== false) {
                    removed.push(viewItems.remove(item));
                }
            });
            if (includeNonClosable) {
                var nonClosableItems = this.nonClosableItems;
                Ext.Array.each(nonClosableItems.items.items, (item:Ext_TopBarMenu_Item) => {
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

            Ext.Array.each(items, (item:Ext_TopBarMenu_Item) => {
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
            return me;
        }

        hide():void {
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
