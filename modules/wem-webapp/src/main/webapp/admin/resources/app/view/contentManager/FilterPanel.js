Ext.define('Admin.view.contentManager.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentFilter',
    cls: 'facet-navigation',

    title: 'Filter',
    split: true,
    collapsible: true,

    initComponent: function () {
        var search = {
            xtype: 'fieldcontainer',
            layout: 'hbox',

            items: [
                {
                    xtype: 'textfield',
                    enableKeyEvents: true,
                    bubbleEvents: ['specialkey'],
                    itemId: 'filterText',
                    name: 'filter',
                    flex: 1
                },
                {
                    xtype: 'button',
                    itemId: 'filterButton',
                    iconCls: 'icon-find',
                    action: 'search',
                    margins: '0 0 0 5'
                }
            ]
        };

        var filter = {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: true,
            bodyPadding: 10,

            defaults: {
                margins: '0 0 0 0'
            },

            items: [
                search,
                {
                    xtype: 'label',
                    text: 'Sites',
                    cls: 'facet-header'
                },

                {
                    xtype: 'checkboxgroup',
                    columns: 1,
                    vertical: true,
                    cls: 'facet-box',

                    defaults: {
                        name: 'type',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'admin-cursor-clickable',
                        width: 170
                    },

                    items: [
                        {
                            boxLabel: 'Travel',
                            inputValue: 'travel',
                            checked: false
                        },
                        {
                            boxLabel: 'Blueman',
                            inputValue: 'Blueman',
                            checked: false
                        },
                        {
                            boxLabel: 'Cityscape',
                            inputValue: 'cityscape',
                            checked: false
                        }
                    ]
                },
                {
                    xtype: 'label',
                    text: '',
                    height: 10
                },
                {
                    xtype: 'label',
                    text: 'Types',
                    cls: 'facet-header'
                },

                {
                    xtype: 'checkboxgroup',
                    columns: 1,
                    vertical: true,
                    cls: 'facet-box',

                    defaults: {
                        name: 'type',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'admin-cursor-clickable',
                        width: 170
                    },

                    items: [
                        {
                            boxLabel: 'News',
                            inputValue: 'news',
                            checked: false
                        },
                        {
                            boxLabel: 'Article',
                            inputValue: 'article',
                            checked: false
                        }
                    ]
                },
                {
                    xtype: 'label',
                    text: '',
                    height: 10
                },
                {
                    xtype: 'label',
                    text: 'Last Modified',
                    cls: 'facet-header',
                    itemId: 'userstoreTitle'
                },
                {
                    xtype: 'checkboxgroup',
                    itemId: 'userstoreOptions',
                    columns: 1,
                    vertical: true,
                    cls: 'facet-box',

                    defaults: {
                        name: 'userStoreKey',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'admin-cursor-clickable',
                        width: 170
                    },

                    items: [
                        {
                            boxLabel: '< 1h',
                            inputValue: 'hour',
                            checked: false
                        },
                        {
                            boxLabel: '< 1d',
                            inputValue: 'day',
                            checked: false
                        },
                        {
                            boxLabel: '< 1w',
                            inputValue: 'week',
                            checked: false
                        },
                        {
                            boxLabel: '> [from date]',
                            inputValue: 'from',
                            checked: false
                        },
                        {
                            boxLabel: '< [to date]',
                            inputValue: 'to',
                            checked: false
                        }
                    ]
                }
            ]
        };

        Ext.apply(this, filter);
        Ext.tip.QuickTipManager.init();

        this.callParent(arguments);
    },


    getValues: function () {
        var value, values = [];

        var checkboxes = Ext.ComponentQuery.query('#filterText, checkbox', this);
        Ext.Array.each(checkboxes, function (checkbox) {
            if (checkbox.getValue()) {
                value = {};
                value[checkbox.getName()] = checkbox.getValue();
                values.push(value);
            }
        });
        return values;
    },


    resetFilter: function () {
        var checkboxes = Ext.ComponentQuery.query('checkbox');
        Ext.Array.each(checkboxes, function (checkbox) {
            checkbox.suspendEvents();
            checkbox.setValue(false);
            checkbox.show();
            checkbox.resumeEvents();
        });

        var filterTextField = this.query('#filterText')[0];
        filterTextField.suspendEvents();
        filterTextField.reset();
        filterTextField.resumeEvents();

        var filterButton = this.query('#filterButton')[0];
        filterButton.fireEvent('click');
    },


    updateTitle: function () {
        var values = this.getValues();
        if (values.length === 0) {

            this.setTitle('Filter');

        } else {

            var title = "Filter   (<a href='javascript:;' class='clearSelection'>Clear filter</a>)";
            this.setTitle(title);

            var clearSel = this.header.el.down('a.clearSelection');
            if (clearSel) {
                clearSel.on("click", function () {
                    this.resetFilter();
                }, this);
            }

        }
    }

});
