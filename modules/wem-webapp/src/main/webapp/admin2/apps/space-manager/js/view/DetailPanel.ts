module app_ui {
    export class SpaceDetailPanel2 extends api_ui_detailpanel.DetailPanel {

        constructor() {
            super();
            var selectedSpaces = app.SpaceContext.get().getSelectedSpaces();
            if (!selectedSpaces || selectedSpaces.length == 0) {
                this.showBlank();
            }

            app_event.GridSelectionChangeEvent.on((event) => {
                this.update(event.getModels());
            })
        }

        showBlank() {
            this.getEl().setInnerHtml("Nothing selected");
        }

        update(models:any[]) {
            if (models.length == 1) {
                this.showSingle(models[0]);
            } else if (models.length > 1) {
                this.showMultiple(models);
            }
        }

        private showSingle(model) {
            this.getEl().setInnerHtml("One selected");
        }

        private showMultiple(models:any[]) {
            this.getEl().setInnerHtml("");
            for (var i in models) {
                this.getEl().appendChild(new api_ui_detailpanel.DetailPanelBox(models[i]).getHTMLElement());
            }
        }
    }

    class DetailPanelTabList extends api_ui.UlEl {
        constructor() {
            super("tab-list");

        }
    }


    export class SpaceDetailPanel {

        ext:Ext_panel_Panel;

        private isVertical:Boolean = false;
        private isFullScreen = false;
        private keyField:String = 'name';
        private data;
        private header;
        private photo;
        private actionMenu;
        private center;

        private tabs = [
            {
                displayName: 'Sales',
                name: 'sales',
                items: [
                    {xtype: 'component', html: '<h1>Sales</h1>'}
                ]
            },
            {
                displayName: 'Scorecard',
                name: 'scorecard',
                items: [
                    {xtype: 'component', html: '<h1>Scorecard</h1>'}
                ]
            },
            {
                displayName: 'History',
                name: 'history',
                items: [
                    {xtype: 'component', html: '<h1>History</h1>'}
                ]
            }
        ];

        constructor(region?:String, id?:string, model?:api_model.SpaceModel) {
            var cls = 'admin-preview-panel admin-detail' + ( this.isVertical ? 'admin-detail-vertical' : '' );
            this.data = model;
            this.isFullScreen = Ext.isEmpty(region);

            var p = new Ext.panel.Panel({
                id: id,
                region: region,
                data: this.data,
                layout: 'card',
                cls: cls,
                border: false,
                split: true,
                collapsible: true,
                header: false,
                flex: 1,
                isVertical: this.isVertical,
                isFullPage: false,
                keyField: this.keyField
            });

            this.ext = <Ext_panel_Panel> p;

            p.on('afterrender', function (detail) {
                detail.el.on('click', function (event, target, opts) {
                    var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button:')[1];
                    detail.fireEvent('deselect', key);
                }, this, {
                    delegate: '.deselect'
                });
                detail.el.on('click', function (event, target, opts) {
                    detail.fireEvent('clearselection');
                }, this, {
                    delegate: '.clearSelection'
                });

                if (detail.isFullPage) {
                    detail.hideActionButton();
                }
                if (this.tabs.length > 0) {
                    this.changeTab(this.tabs[0].name);
                }
            }, this);

            p.add(this.createNoSelectionView());
            p.add(this.createSingleSelectionView(this.data));
            p.add(this.createSmallBoxSelectionView(this.data));
            p.add(this.createLargeBoxSelectionView(this.data));

            this.getLayout().setActiveItem(this.resolveActiveItem());
        }

        private createNoSelectionView() {
            return new Ext.panel.Panel({
                itemId: 'noSelection',
                styleHtmlContent: true,
                padding: 10,
                bodyStyle: { border: 'none' },
                html: '<div>Nothing selected</div>'
            });
        }

        private createSingleSelectionView(data) {
            var c = new Ext.container.Container({
                itemId: 'singleSelection',
                layout: 'border',
                overflowX: 'hidden',
                overflowY: 'hidden'
            });

            var tabNavigation = new Ext.Component({
                data: this.tabs,
                cls: this.isVertical ? 'vertical' : 'horizontal',
                margin: this.isVertical ? '0' : '20 0 0',
                colSpan: 3,
                tpl: [
                    '<ul class="admin-detail-nav">',
                    '<tpl for=".">',
                    '<li data-tab="{name}">{displayName}</li>',
                    '</tpl>',
                    '</ul>'
                ],
                listeners: {
                    click: {
                        element: 'el',
                        fn: (evt, element) => {
                            var tab = element.attributes['data-tab'].value;
                            //var panels = components.detailPanel;
                            this.changeTab(tab);
                            /*for (var i = 0; i < panels.length; i++) {
                             panels[i].changeTab(tab);
                             }*/
                        }
                    }
                }
            });

            var north = new Ext.container.Container({
                region: 'north',
                cls: 'north',
                margin: '5 0',
                height: (this.isVertical ? 100 : 64),
                layout: {
                    type: 'table',
                    tableAttrs: {
                        style: {
                            'table-layout': 'fixed',
                            width: '100%'
                        }
                    },
                    columns: 3
                }
            });

            if (this.isVertical) {
                north.add(tabNavigation);
            }

            var photo = this.photo = new Ext.Component({
                width: 64,
                itemId: 'previewPhoto',
                tpl: '<img src="{data.iconUrl}?size=80" style="width: 64px;" alt="{name}"/>',
                data: data,
                margin: '0 5 0 5',
                tdAttrs: { width: 80 }
            });

            var header = this.header = new Ext.Component({
                itemId: 'previewHeader',
                tpl: '<h1 title="{data.displayName}">{data.displayName}</h1><span class="path" title="{data.path}">{data.path}</span>',
                data: data,
                cls: 'admin-detail-header'
            });

            var actionMenu = this.actionMenu = Ext.apply(
                new app_ui.ActionMenu2().getExt(),
                {
                    tdAttrs: {
                        width: 120,
                        style: 'vertical-align: top;'
                    }
                }
            );

            north.add(photo, header);
            if (!this.isFullScreen) {
                north.add(actionMenu);
            }

            c.add(north);

            var west = new Ext.container.Container({
                region: 'west',
                cls: 'west',
                width: 200
            });

            if (!this.isVertical) {
                west.add(tabNavigation);
            }

            c.add(west);

            var center = this.center = new Ext.container.Container({
                cls: 'center',
                itemId: 'center',
                region: 'center'
            });

            c.add(center);

            return c;
        }

        private createSmallBoxSelectionView(data) {
            return new Ext.Component({
                data: data,
                itemId: 'smallBoxSelection',
                styleHtmlContent: true,
                padding: 10,
                autoScroll: true,
                bodyStyle: { border: 'none' },
                tpl: [
                    '<tpl for=".">',
                    '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box small clearfix">',
                    '<div class="left"><img src="{data.iconUrl}?size=20" alt="{data.name}"/></div>',
                    '<div class="center">{data.displayName}</div>',
                    '<div class="right">',
                    '<a id="remove-from-selection-button:{data.' + this.keyField +
                    '}" class="deselect icon-remove icon-large" href="javascript:;"></a>',
                    '</div>',
                    '</div>',
                    '</tpl>'
                ]
            });
        }

        private createLargeBoxSelectionView(data) {
            return new Ext.Component({
                data: data,
                itemId: 'largeBoxSelection',
                styleHtmlContent: true,
                padding: 10,
                bodyStyle: {
                    border: 'none'
                },
                autoScroll: true,
                tpl: [
                    '<tpl for=".">' +
                    '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box large clearfix">',
                    '<div class="left"><img src="{data.iconUrl}?size=32" alt="{data.name}"/></div>',
                    '<div class="center"><h6>{data.displayName}</h6>',

                    // 18th of April solution!
                    // We should refactor this class so the selection views always gets one data spec
                    '<tpl if="data.path">',
                    '<p>{data.path}</p>',
                    '<tpl elseif="data.description">',
                    '<p>{data.description}</p>',
                    '<tpl elseif="data.name">',
                    '<p>{data.name}</p>',
                    '</tpl>',

                    '</div>',
                    '<div class="right">',
                    '<a id="remove-from-selection-button:{data.' + this.keyField +
                    '}" class="deselect icon-remove icon-2x" href="javascript:;"></a>',
                    '</div>',
                    '</div>',
                    '</tpl>'
                ]
            });
        }

        private resolveActiveItem() {
            var activeItem;
            if (Ext.isEmpty(this.data)) {
                activeItem = 'noSelection';
            } else if (Ext.isObject(this.data) || this.data.length === 1) {
                activeItem = 'singleSelection';
            } else if (this.data.length > 1 && this.data.length <= 10) {
                activeItem = 'largeBoxSelection';
            } else {
                activeItem = 'smallBoxSelection';
            }
            return activeItem;
        }

        private getLayout():Ext_layout_container_Card {
            return <Ext_layout_container_Card> this.ext.getLayout();
        }

        private resolveActiveData(data) {
            var activeData;
            if (Ext.isArray(data) && data.length === 1) {
                activeData = data[0];
            } else {
                activeData = data;
            }
            return activeData;
        }

        private updateActiveItem(data, item) {
            item = item || this.getLayout().getActiveItem();
            if ('singleSelection' === item.itemId) {
                this.header.update(data);
                this.photo.update(data);

                this.changeTab(this.tabs[0].name);

            } else if ('largeBoxSelection' === item.itemId || 'smallBoxSelection' === item.itemId) {
                item.update(data);
            }
        }

        private getTab(name) {
            var tabs = this.tabs;
            for (var tab in tabs) {
                if (tabs[tab].name === name) {
                    return tabs[tab];
                }
            }
            return null;
        }

        private changeTab(selectedTab) {
            var currentTab = this.getTab(selectedTab);
            if (currentTab) {
                var target = this.center;
                // This clears the center everytime we click. This might not be the fastest solution.
                target.remove(target.child());
                if (currentTab.items) {
                    target.add(currentTab.items);
                    if (currentTab.callback) {
                        currentTab.callback(target);
                    }
                }

                var elements = Ext.dom.Query.select('*[data-tab=' + selectedTab + ']');
                for (var i = 0; i < elements.length; i++) {
                    var children = elements[i].parentElement.children;
                    for (var j = 0; j < children.length; j++) {
                        children[j].className = '';
                    }
                    elements[i].className = 'active';
                }
            }
        }

        setData(data) {
            this.data = data;
            var toActivate = this.resolveActiveItem();
            var active = this.getLayout().getActiveItem();
            if (active.getItemId() !== toActivate) {
                active = this.getLayout().setActiveItem(toActivate);
            }
            if (active) {
                var activeData = this.resolveActiveData(data);
                this.updateActiveItem(activeData, active);
            }
        }
    }
}
