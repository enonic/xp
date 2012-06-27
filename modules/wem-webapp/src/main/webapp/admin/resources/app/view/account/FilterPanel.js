Ext.define('Admin.view.account.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accountFilter',
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
                    itemId: 'filter',
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
                    text: 'Type',
                    cls: 'facet-header',
                    itemId: 'accountTypeTitle'
                },

                {
                    xtype: 'checkboxgroup',
                    itemId: 'accountTypeOptions',
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
                            itemId: 'searchFilterUsers',
                            boxLabel: 'Users <span class="count"></span>',
                            inputValue: 'users',
                            checked: false
                        },
                        {
                            itemId: 'searchFilterGroups',
                            boxLabel: 'Groups <span class="count"></span>',
                            inputValue: 'groups',
                            checked: false
                        },
                        {
                            itemId: 'searchFilterRoles',
                            boxLabel: 'Roles <span class="count"></span>',
                            inputValue: 'roles',
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
                    text: 'Userstore',
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
                    ]
                },
                {
                    xtype: 'label',
                    text: '',
                    height: 10
                },
                {
                    xtype: 'label',
                    text: 'Organization',
                    cls: 'facet-header',
                    itemId: 'organizationTitle'
                },
                {
                    xtype: 'checkboxgroup',
                    itemId: 'organizationOptions',
                    columns: 1,
                    vertical: true,
                    cls: 'facet-box',

                    defaults: {
                        name: 'organizations',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'admin-cursor-clickable',
                        width: 170
                    },

                    items: [
                    ]
                }
            ]
        };

        Ext.apply(this, filter);
        Ext.tip.QuickTipManager.init();

        this.callParent(arguments);
    },

    showFacets: function (facets, facetSelected) {
        var facet;
        var i;
        for (i = 0; i < facets.length; i++) {
            facet = facets[i];
            if (facet.name === facetSelected) {
                this.updateSearchFacetsState(facet);
                continue; // skip update of selected facet
            } else {
                this.updateSearchFacets(facet);
            }
        }
    },

    updateSearchFacets: function (facet) {
        if (facet.name === 'userstore') {
            this.showUserstoreFacets(facet);
        } else if (facet.name === 'type') {
            this.showUserTypeFacets(facet);
        } else if (facet.name === 'organization') {
            this.showOrganizationFacets(facet);
        }
    },

    updateSearchFacetsState: function (facet) {
        var checkBoxGroup;
        if (facet.name === 'type') {
            checkBoxGroup = this.down('#accountTypeOptions');
        } else if (facet.name === 'userstore') {
            checkBoxGroup = this.down('#userstoreOptions');
        } else if (facet.name === 'organization') {
            checkBoxGroup = this.down('#organizationOptions');
        }
        if (checkBoxGroup.getChecked().length === 0) {
            this.updateSearchFacets(facet);
        }
    },


    removeAllOrgCheckboxes: function () {
        var organizationCheckGroup = this.query('#organizationOptions')[0];
        organizationCheckGroup.removeAll(true);
    },

    showOrganizationFacets: function (facet) {
        var MAX_ORG_FACET_ITEMS = 10;
        var MAX_ORG_LABEL_CHARS = 20;
        var organizationCheckGroup = this.query('#organizationOptions')[0];
        var checked = organizationCheckGroup.getValue();
        var selectedCheck = {};

        Ext.Object.each(checked, function (key, val) {
            selectedCheck[val] = true;
        });

        this.removeAllOrgCheckboxes();

        var terms = facet.terms;
        var itemId, checkbox, label, tooltip, org;
        var orgList = [];
        var countVisible = 0;
        var organization;
        for (organization in terms) {
            if (terms.hasOwnProperty(organization)) {
                org = {name: organization, hits: terms[organization], checked: selectedCheck[organization] };
                orgList.push(org);
                if (org.checked || (org.hits > 0)) {
                    countVisible++;
                }
            }
        }

        this.query('#organizationTitle')[0].setVisible(countVisible > 0);
        countVisible = Math.min(MAX_ORG_FACET_ITEMS, countVisible);
        orgList = this.sortOrganizationFacets(orgList, countVisible);

        var total = 0;
        var checksToShow = 0;
        var checkSelected;
        Ext.Array.each(orgList, function (org) {
            total++;
            if (total <= MAX_ORG_FACET_ITEMS) {
                checkSelected = org.checked;
                if (checkSelected || (org.hits > 0)) {
                    itemId = org.name + '_org_checkbox';
                    label = Ext.String.ellipsis(org.name, MAX_ORG_LABEL_CHARS) + ' (' + org.hits + ')';
                    var cb = new Ext.form.Checkbox({ itemId: itemId, boxLabel: label, inputValue: org.name, checked: checkSelected, checkedCls: 'x-form-cb-checked facet-selected'});
                    checkbox = organizationCheckGroup.add(cb);

                    if (org.name.length > MAX_ORG_LABEL_CHARS) {
                        tooltip = org.name + ' (' + org.hits + ')';
                        Ext.tip.QuickTipManager.register({
                            target: cb.id,
                            text: tooltip
                        });
                    }

                    checksToShow++;
                }
            } else if (org.hits > 0) {
                checksToShow++;
            }
        });

        if (checksToShow > MAX_ORG_FACET_ITEMS) {
            var text = '... ' + (checksToShow - MAX_ORG_FACET_ITEMS) + ' more';
            organizationCheckGroup.add({
                xtype: 'label',
                html: {
                    tag: 'a',
                    href: 'javascript:;',
                    html: text
                },
                getValue: Ext.emptyFn,
                setValue: Ext.emptyFn
            });
        }
    },

    sortOrganizationFacets: function (orgList, countVisible) {
        // sort array by hits, descending, with stable sorting, checked values should appear on top
        orgList.sort(function (o1, o2) {
            if (o1.checked && o2.checked) {
                return (o2.hits === o1.hits) ? o1.name.localeCompare(o2.name) : (o2.hits - o1.hits);
            } else if (o1.checked) {
                return -1;
            } else if (o2.checked) {
                return 1;
            }
            return (o2.hits === o1.hits) ? o1.name.localeCompare(o2.name) : (o2.hits - o1.hits);
        });

        var top = orgList.slice(0, countVisible);
        var bottom = orgList.slice(countVisible);

        top.sort(function (o1, o2) {
            return (o2.hits === o1.hits) ? o1.name.localeCompare(o2.name) : (o2.hits - o1.hits);
        });

        return top.concat(bottom);
    },

    showUserstoreFacets: function (facet) {

        var terms = facet.terms;
        var itemId, checkbox, count, countVisible = 0;
        var userstore;
        for (userstore in terms) {
            if (terms.hasOwnProperty(userstore)) {
                itemId = this.userstoreCheckboxId(userstore);
                checkbox = Ext.ComponentQuery.query('*[itemId=' + itemId + ']');
                if (checkbox.length > 0) {
                    checkbox = checkbox[0];
                    count = terms[userstore];
                    checkbox.setVisible(checkbox.getValue() || count > 0);
                    if (checkbox.isVisible()) {
                        countVisible++;
                    }
                    checkbox.el.down('span.count').dom.innerHTML = ' (' + count + ')';
                }
            }
        }
        this.query('#userstoreTitle')[0].setVisible(countVisible > 0);
    },

    showUserTypeFacets: function (facet) {
        var userCount = facet.terms.user;
        var groupCount = facet.terms.group;
        var roleCount = facet.terms.role;

        var usersButton = Ext.ComponentQuery.query('*[itemId=searchFilterUsers]')[0];
        usersButton.el.down('span.count').dom.innerHTML = ' (' + userCount + ')';
        usersButton.setVisible(usersButton.getValue() || userCount > 0);

        var groupsButton = Ext.ComponentQuery.query('*[itemId=searchFilterGroups]')[0];
        groupsButton.el.down('span.count').dom.innerHTML = ' (' + groupCount + ')';
        groupsButton.setVisible(groupsButton.getValue() || groupCount > 0);

        var rolesButton = Ext.ComponentQuery.query('*[itemId=searchFilterRoles]')[0];
        rolesButton.el.down('span.count').dom.innerHTML = ' (' + roleCount + ')';
        rolesButton.setVisible(rolesButton.getValue() || roleCount > 0);

        var showTitle = usersButton.isVisible() || groupsButton.isVisible() || rolesButton.isVisible();
        this.query('#accountTypeTitle')[0].setVisible(showTitle);
    },

    setUserStores: function (userstores) {
        var userstoreRadioGroup = Ext.ComponentQuery.query('*[itemId=userstoreOptions]')[0];
        userstoreRadioGroup.removeAll();
        var i;
        for (i = 0; i < userstores.length; i++) {
            var userstore = userstores[i];
            var itemId = this.userstoreCheckboxId(userstore);
            userstoreRadioGroup.add({ itemId: itemId, boxLabel: userstore +
                                                                '<span class="count"></span>', inputValue: userstore, checked: false });
        }
    },

    userstoreCheckboxId: function (userstoreName) {
        return userstoreName + '_checkbox';
    },

    clearFilter: function () {
        this.setTitle('Filter');

        var userstoreCheckboxes = Ext.ComponentQuery.query('[itemId=userstoreOptions] * , [itemId=accountTypeOptions] *, [itemId=organizationOptions] *');
        Ext.Array.each(userstoreCheckboxes, function (checkbox) {
            checkbox.suspendEvents();
            checkbox.setValue(false);
            checkbox.show();
            checkbox.resumeEvents();
        });

        var filterTextField = this.query('#filter')[0];
        filterTextField.suspendEvents();
        filterTextField.reset();
        filterTextField.resumeEvents();

        var filterButton = this.query('#filterButton')[0];
        filterButton.fireEvent('click');
    },

    getSelectedValues: function () {
        var value, values = [];
        var userstoreCheckboxes = Ext.ComponentQuery.query('[itemId=userstoreOptions] * , [itemId=accountTypeOptions] *, [itemId=organizationOptions] *, [itemId=filter]');
        Ext.Array.each(userstoreCheckboxes, function (checkbox) {
            value = checkbox.getValue();
            if (value) {
                values.push(checkbox.getName());
            }
        });
        return values;
    },

    updateTitle: function () {
        var values = this.getSelectedValues();
        if (values.length === 0) {
            this.setTitle('Filter');
        } else {
            var title = "Filter   (<a href='javascript:;' class='clearSelection'>Clear filter</a>)";
            this.setTitle(title);

            var clearSel = this.header.el.down('a.clearSelection');
            if (clearSel) {
                var filterPanel = this;
                clearSel.on("click", function () {
                    filterPanel.clearFilter();
                }, this);
            }
        }
    }

});
