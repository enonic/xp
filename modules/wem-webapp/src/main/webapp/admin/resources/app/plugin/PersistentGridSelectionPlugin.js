/**
 * PersistentGridSelectionPlugin
 * Based on joeri's RowSelectionPaging post,2009-02-26
 *
 * Works on grids and trees using a plain Ext.selection.CheckboxModel
 *
 */
Ext.define('Admin.plugin.PersistentGridSelectionPlugin', {

    extend: 'Ext.util.Observable',
    pluginId: 'persistentGridSelection',
    alias: 'plugin.persistentGridSelection',

    keyField: 'id',

    init: function (panel) {
        this.panel = panel;
        this.selections = [];
        this.selected = {};
        this.ignoreSelectionChanges = '';

        panel.on('render', function () {
            // attach an interceptor for the selModel's onRefresh handler
            this.panel.view.un('refresh', this.panel.selModel.refresh, this.panel.selModel);
            this.panel.view.on('refresh', this.onViewRefresh, this);
            this.panel.view.on('beforeitemmousedown', function (view, record, item, index, event, eOpts) {
                // It is not possible to check the checkbox in CheckboxModel when left clicking the column.
                // Not sure if this is a bug since right clicking the column does check the checkbox (or shift/ctrl + left click).
                // Forum thread: http://www.sencha.com/forum/showthread.php?173519-Grid-and-checkbox-column-click
                var targetElement = new Ext.Element(event.target);
                var isCheckboxColumnIsClicked = targetElement.findParent('td.x-grid-cell-first') !== null;
                if (isCheckboxColumnIsClicked) {
                    var isShiftKeyPressed = event.shiftKey === true;
                    var isCtrlKeyPressed = event.ctrlKey === true;
                    // It works when using the shift and meta keys. Just return if this is the case
                    if (isShiftKeyPressed || isCtrlKeyPressed) {
                        return;
                    }

                    var isChecked = this.selected[record.get(this.keyField)];
                    if (!isChecked) {
                        this.panel.selModel.select(index, true, false);
                    }
                    else {
                        this.panel.selModel.deselect(index);
                    }

                    return false;
                }

                this.clearSelectionOnRowClick(view, record, item, index, event, eOpts);
                this.cancelItemContextClickWhenSelectionIsMultiple(view, record, item, index, event, eOpts);
            }, this);
            this.panel.view.headerCt.on('headerclick', this.onHeaderClick, this);
            // add a handler to detect when the user changes the selection
            this.panel.selModel.on('select', this.onRowSelect, this);
            this.panel.selModel.on('deselect', this.onRowDeselect, this);
            this.panel.getStore().on('beforeload', function () {
                this.ignoreSelectionChanges = true;
            }, this);

            // additional tree events
            this.panel.view.on('itemadd', this.onViewRefresh, this);

            var pagingToolbar = this.panel.down('pagingtoolbar');
            if (pagingToolbar !== null) {
                pagingToolbar.on('beforechange', this.pagingOnBeforeChange, this);
            }
        }, this);
    },

    /**
     * Returns the selected records for all pages
     * @return {Array} Array of selected records
     */
    getSelection: function () {
        return [].concat(this.selections);
    },

    /**
     * Returns the selection count for all pages
     * @return {Number} Number of selected records
     */
    getSelectionCount: function () {
        return this.getSelection().length;
    },

    /**
     * Removes an record from the selection
     * @param {Ext.data.Model} record The selected record
     */
    deselect: function (record) {
        this.onRowDeselect(this.panel.selModel, record);

        // If the deselected item is on the current page we need to programmatically deselect it.
        // First get the item object from the store (the record argument in this method is not the same object since the page has been refreshed)
        var storeRecord;
        var key = record.get(this.keyField);
        if (this.panel instanceof Ext.tree.Panel) {
            storeRecord = this.panel.getRootNode().findChild(this.keyField, key);
        } else if (this.panel instanceof Ext.grid.Panel) {
            storeRecord = this.panel.getStore().findRecord(this.keyField, key);
        }

        this.panel.selModel.deselect(storeRecord);

        // Tell the selection model about a change.
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * Selects all the rows in the grid, including those on other pages
     * Be very careful using this on very large datasets
     */
    selectAll: function () {
        this.panel.selModel.selectAll();
    },

    /**
     * Clears selections across all pages
     */
    clearSelection: function () {
        this.selections = [];
        this.selected = {};
        this.panel.selModel.deselectAll();
        this.onViewRefresh();
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * @private
     */
    onViewRefresh: function () {
        this.ignoreSelectionChanges = true;
        // explicitly refresh the selection model
        this.panel.selModel.refresh();
        // selection changed from view updates, restore full selection

        var i;
        var sm = this.panel.getSelectionModel();

        if (this.panel instanceof Ext.tree.Panel) {
            var rootNode = this.panel.getRootNode(),
                node;

            for (var selectedItem in this.selected) {
                if (this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    node = rootNode.findChild(this.keyField, selectedItem, true);
                    if (node) {
                        sm.select(node, true);
                    }
                }
            }
        } else if (this.panel instanceof Ext.grid.Panel) {
            var store = this.panel.getStore(),
                record;

            for (var selectedItem in this.selected) {
                if (this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    record = store.findRecord(this.keyField, selectedItem);
                    if (record) {
                        sm.select(record, true);
                    }
                }
            }
        }
        this.ignoreSelectionChanges = false;
    },

    /**
     * @private
     */
    pagingOnBeforeChange: function () {
        this.ignoreSelectionChanges = true;
    },

    /**
     * @private
     */
    onSelectionClear: function () {
        if (!this.ignoreSelectionChanges) {
            // selection cleared by user
            // also called internally when the selection replaces the old selection
            this.selections = [];
            this.selected = {};
        }
    },

    /**
     * @private
     */
    onRowSelect: function (sm, rec, i, o) {
        if (!this.ignoreSelectionChanges) {
            if (!this.selected[rec.get(this.keyField)]) {
                this.selections.push(rec);
                this.selected[rec.get(this.keyField)] = true;
            }

        }
    },

    /**
     * @private
     */
    onHeaderClick: function (headerCt, header, e) {
        if (header.isCheckerHd) {
            e.stopEvent();
            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
            if (isChecked) {
                this.clearSelection();
            } else {
                this.panel.selModel.selectAll();
            }
        }

        return false;
    },

    /**
     * @private
     */
    onRowDeselect: function (rowModel, record, index, eOpts) {
        if (!this.ignoreSelectionChanges) {
            if (this.selected[record.get(this.keyField)]) {
                for (var j = this.selections.length - 1; j >= 0; j--) {
                    if (this.selections[j].get(this.keyField) == record.get(this.keyField)) {
                        this.selections.splice(j, 1);
                        this.selected[record.get(this.keyField)] = false;
                        break;
                    }
                }
            }
        }
    },

    /**
     * @private
     */
    notifySelectionModelAboutSelectionChange: function () {
        this.panel.selModel.fireEvent("selectionchange", this.panel.selModel, this.selections);
    },

    /**
     * @private
     */
    cancelItemContextClickWhenSelectionIsMultiple: function (view, record, item, index, event, eOpts) {
        var isRightClick = event.button === 2;
        var recordIsSelected = this.selected[record.get(this.keyField)];
        var cancel = isRightClick && recordIsSelected && this.getSelectionCount() > 1;

        if (cancel) {
            return false;
        }
        return true;
    },

    /**
     * @private
     */
    clearSelectionOnRowClick: function (view, record, item, index, event, eOpts) {
        var targetElement = event.target;
        var isLeftClick = event.button === 0;
        var isCheckbox = targetElement.className && targetElement.className.indexOf('x-grid-row-checker') > -1;

        if (isLeftClick && !isCheckbox) {
            this.clearSelection();
        }
    }

});
