Ext.define('Admin.view.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.filterPanel',

    cls: 'admin-filter',
    header: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    autoScroll: true,
    split: true,

    includeSearch: true,

    /**
     * @param includeEmptyFacets defines whether facets with 0 count should be shown
     * all - always show empty facets,
     * last - show empty facets only for last modified group,
     * none - hide empty facets
     */
    includeEmptyFacets: 'none',

    /**
     * @param updateCountCriteria defines when to update facet count
     * always - update count every time the filter is changed
     * query - update when query is made only
     * never - set counts just once on start
     */
    updateCountCriteria: 'always',

    /**
     * @param updateCountStrategy defines what facet counts should be updated
     * all - update counts in all groups
     * notlast - update counts for all except the last modified group
     */
    updateCountStrategy: 'notlast',

    facetTpl: undefined,
    facetData: undefined,

    initComponent: function () {
        var me = this;

        if (!Ext.isEmpty(this.title)) {
            this.originalTitle = this.title;
        }

        Ext.applyIf(this, {
            items: [],
            facetTpl: new Ext.XTemplate(
                '<tpl for=".">',
                '<div class="admin-facet-group" name="{name}">',
                '<h2>{name}</h2>',
                '<tpl for="terms">{[this.updateFacetCount(values, parent)]}',
                '<tpl if="this.shouldShowTerm(values, parent)">',
                '<div class="admin-facet {[values.selected ? \'checked\' : \'\']}">',
                '<input type="checkbox" id="facet-{term}" value="{name}" class="admin-facet-cb" name="{parent.name}" {[values.selected ? \'checked="true"\' : \'\']} />',
                '<label for="facet-{key}" class="admin-facet-lbl"> {[values.displayName || values.name]} ({[this.getTermCount(values)]})</label>',
                '</div>',
                '</tpl>',
                '</tpl>',
                '</div>',
                '</tpl>',
                {
                    updateFacetCount: function (term, facet) {

                        // when to update
                        var isCriteria = me.updateCountCriteria == 'always' || (me.updateCountCriteria == 'query' && me.queryDirty);

                        // what to update
                        var isStrategy = me.updateCountStrategy == 'all' ||
                                         (me.updateCountStrategy == 'notlast' && me.lastFacetName != facet.name);

                        var isDefined = Ext.isDefined(me.facetCountMap[term.name]);

                        if (!me.isDirty() || !isDefined || ( isCriteria && isStrategy )) {

                            me.facetCountMap[term.name] = term.count;
                        }
                    },

                    shouldShowTerm: function (term, facet) {

                        // decide if it should be shown
                        return me.includeEmptyFacets == 'all' ||
                               (me.includeEmptyFacets == 'last' && (!me.lastFacetName || me.lastFacetName == facet.name)) ||
                               me.facetCountMap[term.name] > 0 || term.selected || this.isSelected(term, facet);
                    },

                    getTermCount: function (term) {
                        return me.facetCountMap[term.name];
                    },

                    isSelected: function (term, facet) {
                        var terms = me.selectedValues[facet.name];
                        if (terms) {
                            return Ext.Array.contains(terms, term.name);
                        }
                        return false;
                    }
                }
            )
        });

        this.facetContainer = Ext.create('Ext.Component', {
            xtype: 'component',
            itemId: 'facetContainer',
            tpl: me.facetTpl,
            data: me.facetData,
            listeners: {
                afterrender: function (cmp) {
                    cmp.el.on('click', me.onFacetClicked, me, {
                        delegate: '.admin-facet'
                    });
                }
            }
        });
        this.items.unshift(this.facetContainer);

        this.clearLink = Ext.create('Ext.Component', {
            xtype: 'component',
            html: '<a href="javascript:;">Clear filter</a>',
            listeners: {
                click: {
                    element: 'el',
                    fn: me.reset,
                    scope: me
                },
                afterrender: function (cmp) {
                    // hiding() with hideMode: 'visibility' doesn't retain components space
                    // so we manually operate el.visibility
                    cmp.el.setStyle('visibility', 'hidden');
                }
            }
        });

        this.items.unshift(this.clearLink);

        if (this.includeSearch) {

            this.searchField = Ext.create('Ext.form.field.Text', {
                xtype: 'textfield',
                cls: 'admin-search-trigger',
                enableKeyEvents: true,
                bubbleEvents: ['specialkey'],
                itemId: 'filterText',
                margin: '0 0 10 0',
                name: 'query',
                emptyText: 'Search',
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
            });
            this.items.unshift(this.searchField);
        }

        this.facetCountMap = [];
        this.callParent(arguments);
        this.addEvents('search', 'reset');
    },

    onKeyPressed: function (field, event, opts) {
        if (this.suspendEvents !== true) {
            if (event.getKey() === event.ENTER) {
                if (event.type === "keydown") {
                    this.fireEvent('search', this.getValues());
                }
            } else {
                var me = this;
                if (this.searchFilterTypingTimer !== null) {
                    window.clearTimeout(this.searchFilterTypingTimer);
                    this.searchFilterTypingTimer = null;
                }
                this.searchFilterTypingTimer = window.setTimeout(function () {
                    if (me.updateCountCriteria === 'query') {
                        me.queryDirty = true;
                    }
                    if (me.includeEmptyFacets == 'last') {
                        me.lastFacetName = undefined;
                    }
                    me.search();
                }, 500);
            }
        }
    },

    onFacetClicked: function (event, target, opts) {

        target = Ext.fly(target);
        var facet = target.hasCls('admin-facet') ? target : target.up('.admin-facet');
        if (facet) {

            var cb = facet.down('input[type=checkbox]', true);
            var checked = cb.hasAttribute("checked");
            if (checked) {
                cb.removeAttribute("checked");
                facet.removeCls("checked");
            } else {
                cb.setAttribute("checked", "true");
                facet.addCls("checked");
            }

            var group = facet.up('.admin-facet-group', true);
            if (group) {
                this.lastFacetName = group.getAttribute('name');
            }

            this.search();
        }

        event.stopEvent();
        return true;
    },

    updateFacets: function (facets) {
        if (facets) {
            this.selectedValues = this.getValues();
            this.down('#facetContainer').update(facets);
            this.setValues(this.selectedValues);
        }
    },

    getValues: function () {
        var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        var values = {};
        if (this.searchField) {
            var query = this.searchField.getValue();
            if (Ext.String.trim(query).length > 0) {
                values[this.searchField.name] = query;
            }
        }
        Ext.Array.each(selectedCheckboxes, function (cb) {
            var oldValue = values[cb.name];
            if (Ext.isArray(oldValue)) {
                oldValue.push(cb.value);
            } else {
                values[cb.name] = [cb.value];
            }
        });

        return values;
    },

    setValues: function (values) {
        var me = this;

        if (this.searchField) {
            this.searchField.setValue(values[this.searchField.name]);
        }

        var checkboxes = Ext.query('.admin-facet-group input[type=checkbox]', this.facetContainer.el.dom);
        var checkedCount = 0, facet;
        Ext.Array.each(checkboxes, function (cb) {
            var facet = Ext.fly(cb).up('.admin-facet');
            if (me.isValueChecked(cb.value, values)) {
                checkedCount++;
                cb.setAttribute('checked', 'true');
                facet.addCls('checked');
            } else {
                cb.removeAttribute('checked');
                facet.removeCls('checked');
            }
        });

        if (this.updateCountCriteria == 'query' && this.queryDirty && checkedCount === 0) {
            this.queryDirty = false;
        }

    },

    isValueChecked: function (value, values) {
        for (var facet in values) {
            if (values.hasOwnProperty(facet)) {
                var terms = [].concat(values[facet]);
                for (var i = 0; i < terms.length; i++) {
                    if (terms[i] === value) {
                        return true;
                    }
                }
            }
        }
        return false;
    },

    isDirty: function () {
        var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        var query = Ext.String.trim(this.searchField.getValue());
        return selectedCheckboxes.length > 0 || query.length > 0;
    },

    reset: function () {

        if (this.fireEvent('reset', this.isDirty()) !== false) {

            if (this.searchField) {
                this.searchField.reset();
            }

            var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
            Ext.Array.each(selectedCheckboxes, function (cb) {
                cb.removeAttribute('checked');
                Ext.fly(cb).up('.admin-facet').removeCls('checked');
            });

            this.clearLink.el.setStyle('visibility', 'hidden');

            if (this.includeEmptyFacets == 'last') {
                this.lastFacetName = undefined;
            }
        }

    },

    search: function () {

        if (this.fireEvent('search', this.getValues()) !== false) {
            this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
        }

    }

});
