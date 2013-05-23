module admin.ui {
    export class FilterPanel {

        private ext;

        private facetData = [
            {
                "name": 'Space',
                "terms": [
                    { "name": 'Public Web', "key": 'public', "count": 8 },
                    { "name": 'Intranet', "key": 'intra', "count": 20 }
                ]
            },
            {
                "name": "Type",
                "terms": [
                    { "name": "Space", "key": "space", "count": 10 },
                    { "name": "Part", "key": "part", "count": 80 },
                    { "name": "Page Template", "key": "template", "count": 7 }
                ]
            },
            {
                "name": "Module",
                "terms": [
                    { "name": "Twitter Bootrstrap", "key": "twitter", "count": 0 },
                    { "name": "Enonic", "key": "enonic", "count": 3 },
                    { "name": "Foo", "key": "foo", "count": 6 }
                ]
            }
        ];

        private facetTpl = '<tpl for=".">' +
                           '<div class="admin-facet-group" name="{name}">' +
                           '<h2>{[values.displayName || values.name]}</h2>' +
                           '<tpl for="terms">{[this.updateFacetCount(values, parent)]}' +
                           '<tpl if="this.shouldShowTerm(values, parent)">' +
                           '<div class="admin-facet {[values.selected ? \'checked\' : \'\']}">' +
                           '<input type="checkbox" id="facet-{term}" value="{name}" class="admin-facet-cb" name="{parent.name}" {[values.selected ? \'checked="true"\' : \'\']} />' +
                           '<label for="facet-{key}" class="admin-facet-lbl"> {[values.displayName || values.name]} ({[this.getTermCount(values)]})</label>' +
                           '</div>' +
                           '</tpl>' +
                           '</tpl>' +
                           '</div>' +
                           '</tpl>';

        constructor(config?:{
            region?: string;
            width?: number;
            includeSearch?: bool;
        }) {

            var updateFacets = function (facets) {
                if (facets) {
                    this.selectedValues = this.getValues();
                    this.down('#facetContainer').update(facets);
                    this.setValues(this.selectedValues);
                }
            };

            var getValues = function () {
                var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                var values = {};
                if (this.searchField) {
                    var query = this.searchField.getValue();
                    if (Ext.String.trim(query).length > 0) {
                        values[this.searchField.name] = query;
                    }
                }
                Ext.Array.each(selectedCheckboxes, function (cb:Html_dom_Element, index, all) {
                    var oldValue = values[cb.name];
                    if (Ext.isArray(oldValue)) {
                        oldValue.push(cb.value);
                    } else {
                        values[cb.name] = [cb.value];
                    }
                });

                return values;
            };

            var setValues = function (values) {
                var me = this;

                if (this.searchField) {
                    this.searchField.setValue(values[this.searchField.name]);
                }

                var checkboxes = Ext.query('.admin-facet-group input[type=checkbox]', this.facetContainer.el.dom);
                var checkedCount = 0, facet;
                Ext.Array.each(checkboxes, function (cb:Html_dom_Element) {
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
            };

            var isValueChecked = function (value, values) {
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
            };

            var isDirty = function () {
                var selectedCheckboxes = [];
                var query = '';
                if (this.facetContainer && this.facetContainer.el) {
                    selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                }
                if (this.searchField) {
                    query = Ext.String.trim(this.searchField.getValue());
                }
                return selectedCheckboxes.length > 0 || query.length > 0;
            };

            var search = function () {
                if (this.fireEvent('search', this.getValues()) !== false) {
                    this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
                }
            };

            var includeSearch = config && (typeof config.includeSearch !== "undefined") ? config.includeSearch : true;

            var fp = this.ext = new Ext.panel.Panel({
                region: config ? config.region : undefined,
                width: config ? config.width : undefined,
                cls: 'admin-filter',
                header: false,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                autoScroll: true,
                split: true,

                facetCountMap: [],
                includeSearch: includeSearch,
                includeEmptyFacets: 'none',
                updateCountCriteria: 'always',
                updateCountStrategy: 'notlast',

                originalTitle: '',

                updateFacets: updateFacets,
                getValues: getValues,
                setValues: setValues,
                isValueChecked: isValueChecked,
                isDirty: isDirty,
                search: search
            });

            var facetContainer = this.createFacetContainer();
            fp.insert(0, facetContainer);

            var clearLink = this.createClearLink();
            fp.insert(0, clearLink);

            Ext.apply(fp, {
                facetContainer: facetContainer,
                clearLink: clearLink
            });

            if (includeSearch) {

                var searchField = this.createSearchField();
                fp.insert(0, searchField);

                Ext.apply(fp, {
                    searchField: searchField
                });
            }

            fp.addEvents('search', 'reset');
        }

        private createFacetContainer() {
            var fp = this.ext;
            var onFacetClicked = function (event, target, opts) {
                target = Ext.fly(target);
                var facet = target.hasCls('admin-facet') ? target : target.up('.admin-facet');
                if (facet) {

                    var cb:Html_dom_Element = facet.down('input[type=checkbox]', true);
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
            };

            var facetContainer = new Ext.Component({
                itemId: 'facetContainer',
                tpl: new Ext.XTemplate(this.facetTpl, {
                    updateFacetCount: function (term, facet) {

                        // when to update
                        var isCriteria = fp.updateCountCriteria == 'always' || (fp.updateCountCriteria == 'query' && fp.queryDirty);

                        // what to update
                        var isStrategy = fp.updateCountStrategy == 'all' ||
                                         (fp.updateCountStrategy == 'notlast' && fp.lastFacetNafp != facet.nafp);

                        var isDefined = Ext.isDefined(fp.facetCountMap[term.name]);
                        var isDirty = fp.isDirty();

                        if (!isDirty || !isDefined || ( isCriteria && isStrategy )) {
                            fp.facetCountMap[term.name] = term.count;
                        }
                    },

                    shouldShowTerm: function (term, facet) {

                        // decide if it should be shown
                        return fp.includeEmptyFacets == 'all' ||
                               (fp.includeEmptyFacets == 'last' && (!fp.lastFacetName || fp.lastFacetName == facet.name)) ||
                               fp.facetCountMap[term.name] > 0 || term.selected || this.isSelected(term, facet);
                    },

                    getTermCount: function (term) {
                        return fp.facetCountMap[term.name];
                    },

                    isSelected: function (term, facet) {
                        var terms = fp.selectedValues[facet.name];
                        if (terms) {
                            return Ext.Array.contains(terms, term.name);
                        }
                        return false;
                    }
                }),
                data: this.facetData
            });
            facetContainer.on('afterrender', function (cmp) {
                cmp.el.on('click', onFacetClicked, fp, {
                    delegate: '.admin-facet'
                });
            });

            return facetContainer;
        }

        private createClearLink() {
            var reset = function () {
                if (this.fireEvent('reset', this.isDirty()) !== false) {

                    if (this.searchField) {
                        this.searchField.reset();
                    }

                    var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                    Ext.Array.each(selectedCheckboxes, function (cb:Html_dom_Element) {
                        cb.removeAttribute('checked');
                        Ext.fly(cb).up('.admin-facet').removeCls('checked');
                    });

                    this.clearLink.el.setStyle('visibility', 'hidden');

                    this.lastFacetName = undefined;
                }
            };

            var clearLink = new Ext.Component({
                html: '<a href="javascript:;">Clear filter</a>'
            });
            clearLink.on('click', reset, this.ext, {element: 'el'});
            clearLink.on('afterrender', function (cmp) {
                // hiding() with hideMode: 'visibility' doesn't retain components space
                // so we manually operate el.visibility
                cmp.el.setStyle('visibility', 'hidden');
            });
            return clearLink;
        }

        private createSearchField() {
            var onKeyPressed = function (field, event, opts) {
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
                            me.lastFacetName = undefined;
                            me.search();
                        }, 500);
                    }
                }
            };

            var searchField = new Ext.form.field.Text({
                cls: 'admin-search-trigger',
                enableKeyEvents: true,
                bubbleEvents: ['specialkey'],
                itemId: 'filterText',
                margin: '0 0 10 0',
                name: 'query',
                emptyText: 'Search'
            });

            searchField.on('specialkey', onKeyPressed, this.ext);
            searchField.on('keypress', onKeyPressed, this.ext);

            return searchField;
        }

        public getExtEl() {
            return this.ext;
        }

    }

}
