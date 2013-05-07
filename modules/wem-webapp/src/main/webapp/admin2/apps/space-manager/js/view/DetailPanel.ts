module admin.ui {
    export class SpaceDetailPanel {

        ext;

        private isVertical:Boolean = false;
        private keyField:String = 'name';
        private data;
        private header;
        private photo;
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

        constructor(region?:String) {
            var p = this.ext = new Ext.panel.Panel();
            if (region) {
                p.region = region;
            }
            p.layout = 'card';
            p.cls = 'admin-preview-panel admin-detail';
            p.border = false;
            p.split = true;
            p.collapsible = true;
            p.header = false;
            p.flex = 1;
            p.isVertical = this.isVertical;
            p.isFullPage = false;
            p.keyField = this.keyField;
            p.listeners = {
                afterrender: function (detail) {
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

                    if (this.isFullPage) {
                        this.hideActionButton();
                    }
                    if (this.tabs.length > 0) {
                        this.changeTab(this.tabs[0].name);
                    }
                }
            };

            if (p.isVertical) {
                p.cls = p.cls + 'admin-detail-vertical';
            }

            p.add(this.noSelection());
            p.add(this.singleSelectionComponent(this.data));
            p.add(this.smallBoxSelection(this.data));
            p.add(this.largeBoxSelection(this.data));
        }

        private noSelection() {
            var p = new Ext.panel.Panel();
            p.itemId = 'noSelection';
            p.styleHtmlContent = true;
            p.padding = 10;
            p.bodyStyle = { border: 'none' };
            p.html = '<div>Nothing selected</div>';

            return p;
        }

        private singleSelectionComponent(data) {
            var c = new Ext.container.Container();
            c.itemId = 'singleSelection';
            c.layout = 'border';
            c.overflowX = 'hidden';
            c.overflowY = 'hidden';

            var tabNavigation = new Ext.Component({
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
            tabNavigation.cls = this.isVertical ? 'vertical' : 'horizontal';
            tabNavigation.margin = this.isVertical ? '0' : '20 0 0';
            tabNavigation.colSpan = 3;
            tabNavigation.tpl = new Ext.XTemplate('<ul class="admin-detail-nav">',
                '<tpl for=".">',
                '<li data-tab="{name}">{displayName}</li>',
                '</tpl>',
                '</ul>');
            tabNavigation.data = this.tabs;


            var north = new Ext.container.Container();
            north.region = 'north';
            north.cls = 'north';
            north.margin = '5 0';
            north.height = (this.isVertical ? 100 : 64);
            north.layout = {
                type: 'table',
                tableAttrs: {
                    style: {
                        tableLayout: 'fixed',
                        width: '100%'
                    }
                },
                columns: 2
            };

            if (this.isVertical) {
                north.add(tabNavigation);
            }

            var photo = this.photo = new Ext.Component();
            photo.width = 64;
            photo.itemId = 'previewPhoto';
            photo.tpl = '<img src="{data.iconUrl}?size=80" style="width: 64px;" alt="{name}"/>';
            photo.data = data;
            photo.margin = '0 5 0 5';
            photo.tdAttrs = { width: 80 };

            north.add(photo);


            var header = this.header = new Ext.Component();
            header.itemId = 'previewHeader';
            header.tpl = '<h1 title="{data.displayName}">{data.displayName}</h1><span class="path" title="{data.path}">{data.path}</span>';
            header.data = data;
            header.cls = 'admin-detail-header';

            north.add(header);

            //TODO: Add actionbutton (dropdownButton)

            c.add(north);

            var west = new Ext.container.Container();
            west.region = 'west';
            west.cls = 'west';
            west.width = 200;

            if (!this.isVertical) {
                west.add(tabNavigation);
            }

            c.add(west);

            var center = this.center = new Ext.container.Container();
            center.cls = 'center';
            center.itemId = 'center';
            center.region = 'center';

            c.add(center);

            return c;
        }

        private smallBoxSelection(data) {
            var c = new Ext.Component();

            c.itemId = 'smallBoxSelection';
            c.styleHtmlContent = true;
            c.padding = 10;
            c.autoScroll = true;
            c.bodyStyle = { border: 'none' };
            c.tpl = ['<tpl for=".">',
                '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box small clearfix">',
                '<div class="left"><img src="{data.iconUrl}?size=20" alt="{data.name}"/></div>',
                '<div class="center">{data.displayName}</div>',
                '<div class="right">',
                '<a id="remove-from-selection-button:{data.' + this.keyField +
                '}" class="deselect icon-remove icon-large" href="javascript:;"></a>',
                '</div>',
                '</div>',
                '</tpl>'];
            c.data = data;

            return c;
        }

        private largeBoxSelection(data) {
            var c = new Ext.Component();
            c.itemId = 'largeBoxSelection';
            c.styleHtmlContent = true;
            c.padding = 10;
            c.bodyStyle = {
                border: 'none'
            };
            c.autoScroll = true;
            c.tpl = [
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
            c.data = data;
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

        private getLayout() {
            return this.ext.getLayout();
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
            console.log(data);
            this.data = data;
            var toActivate = this.resolveActiveItem();
            var active = this.getLayout().getActiveItem();
            if (active.itemId !== toActivate) {
                active = this.getLayout().setActiveItem(toActivate);
            }
            if (active) {
                var activeData = this.resolveActiveData(data);
                this.updateActiveItem(activeData, active);
            }
        }
    }
}
