Ext.define('Admin.view.FilterPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.filterPanel',

    title: 'Filter',
    cls: 'facet-navigation',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    bodyPadding: 10,
    split: true,
    collapsible: true,
    border: true,
    autoScroll: true,

    includeSearch: true,
    includeFacets: undefined,
    includeEmptyFacets: 'all', // all, last, none

    statics: {
        facetDefaults: {
            cls: 'facet-single-select-item',
            checkedCls: 'x-form-cb-checked facet-selected',
            overCls: 'admin-cursor-clickable',
            checked: false
        }
    },

    initComponent: function () {
        var me = this;

        if (!Ext.isEmpty(this.title)) {
            this.originalTitle = this.title;
        }

        if (!Ext.isArray(this.items)) {
            this.items = [];
        }

        if (this.includeSearch) {
            this.items.unshift({
                xtype: 'fieldcontainer',
                layout: 'hbox',
                cls: 'search-box',

                items: [
                    {
                        xtype: 'textfield',
                        enableKeyEvents: true,
                        bubbleEvents: ['specialkey'],
                        itemId: 'filterText',
                        name: 'query',
                        flex: 1,
                        listeners: {
                            specialkey: {
                                fn: me.onKeyPressed,
                                scope: me
                            },
                            keypress: {
                                fn: me.onKeyPressed,
                                scope: me
                            }
                        }
                    },
                    {
                        xtype: 'button',
                        itemId: 'filterButton',
                        iconCls: 'icon-find',
                        action: 'search',
                        margins: '0 0 0 5',
                        listeners: {
                            click: {
                                fn: me.onFacetChanged,
                                scope: me
                            }
                        }
                    }
                ]
            });
        }

        if (this.includeFacets) {
            this.addFacets(this.includeFacets);
        }

        this.callParent(arguments);

        this.addEvents('search');
    },


    onKeyPressed: function (field, event, opts) {
        if (this.suspendEvents !== true) {
            if (event.getKey() === event.ENTER) {
                this.updateTitle();
                this.fireEvent('search', this.getValues());
            } else {
                var me = this;
                if (this.searchFilterTypingTimer !== null) {
                    window.clearTimeout(this.searchFilterTypingTimer);
                    this.searchFilterTypingTimer = null;
                }
                this.searchFilterTypingTimer = window.setTimeout(function () {
                    me.updateTitle();
                    me.fireEvent('search', me.getValues());
                }, 500);
            }
        }
    },

    onFacetChanged: function (facet, newVal, oldVal, opts) {
        if (this.suspendEvents !== true) {

            if (this.includeEmptyFacets === 'last') {
                this.lastFacetName = facet.name;
            }

            this.updateTitle();
            this.fireEvent('search', this.getValues());
        }
    },

    updateTitle: function () {
        if (!this.getForm().isDirty()) {
            this.setTitle(this.originalTitle);
        } else {

            var title = this.originalTitle + " (<a href='javascript:;' class='clearSelection'>Clear filter</a>)";
            this.setTitle(title);

            var clearSel = this.header.el.down('a.clearSelection');
            if (clearSel) {
                clearSel.on("click", function () {
                    // stop events to prevent firing change events by every field
                    this.resetValues();
                    this.fireEvent('search', this.getValues());
                }, this);
            }

        }
    },


    removeFacets: function () {
        var me = this;
        this.items.each(function (item) {
            var isFacet = item && item.cls && item.cls.indexOf('facet') >= 0;
            if (isFacet) {
                if (!me.rendered) {
                    me.items.remove(item);
                } else {
                    me.remove(item);
                }
            }
        });
    },

    addFacets: function (facets) {
        var i = 0;
        var me = this;
        for (i = 0; i < facets.length; i++) {

            var facet = facets[i];
            var facetItems;

            if (facet.terms) {
                facetItems = [];
                var field;
                for (field in facet.terms) {
                    if (facet.terms.hasOwnProperty(field)) {
                        var termCount = parseInt(facet.terms[field], 10);
                        if (me.includeEmptyFacets === 'all' ||
                            (me.includeEmptyFacets === 'last' && me.lastFacetName === facet.name) ||
                            termCount > 0) {

                            facetItems.push({
                                name: facet.name,
                                boxLabel: field + "<span class='count'>(" + facet.terms[field] + ")</span>",
                                inputValue: field
                            });
                        }
                    }
                }
            } else {
                facetItems = facet.items;
            }

            if (facetItems.length > 0) {
                var facetConfig = [
                    {
                        xtype: 'label',
                        text: facet.title || facet.name,
                        cls: 'facet-header'
                    },
                    {
                        xtype: facet.xtype || 'checkboxgroup',
                        name: facet.name,
                        columns: 1,
                        vertical: true,
                        cls: 'facet-box',
                        defaults: Admin.view.FilterPanel.facetDefaults,
                        items: facetItems,
                        listeners: {
                            change: {
                                fn: me.onFacetChanged,
                                scope: me
                            }
                        }
                    }
                ];
                if (!this.rendered) {
                    this.items = this.items.concat(facetConfig);
                } else {
                    this.add(facetConfig);
                }
            }
        }
    },

    updateFacets: function (facets) {
        // suspend events to prevent firing change event for every field
        this.suspendEvents = true;
        // backup filter state
        var data = this.getValues();
        this.removeFacets();
        this.addFacets(facets);
        // restore filter state
        this.setValues(data);

        this.suspendEvents = false;
    },


    getValues: function () {
        return this.getForm().getValues();
    },

    setValues: function (values) {

        var form = this.getForm();

        Ext.iterate(values, function (fieldId, val) {
            var field = form.findField(fieldId);
            if (field) {
                if (field.xtype === 'checkbox') {
                    var cbgroup = field.up('checkboxgroup');
                    if (cbgroup) {
                        field = cbgroup;
                    }
                }
                if (field.xtype === 'checkboxgroup') {
                    var temp = {};
                    temp[fieldId] = val;
                    val = temp;
                }
                field.setValue(val);
            }
        });

        this.updateTitle();
    },

    resetValues: function () {
        // suspend events to prevent firing change event for every field
        this.suspendEvents = true;
        this.getForm().reset();
        this.suspendEvents = false;

        this.updateTitle();
    },

    isDirty: function () {
        return this.getForm().isDirty();
    }

});
