/**
 * PersistentGridSelectionPlugin
 * Based on joeri's RowSelectionPaging post,2009-02-26
 *
 * Only tested on grids using a plain Ext.selection.CheckboxModel
 */
Ext.define('Admin.plugin.PersistentGridSelectionPlugin', {

    extend: 'Ext.util.Observable',
    pluginId: 'persistentGridSelection',
    alias: 'plugin.persistentGridSelection',

    init: function (grid) {
        this.grid = grid;
        this.selections = [];
        this.selected = {};
        this.ignoreSelectionChanges = '';

        grid.on('render', function () {
            // attach an interceptor for the selModel's onRefresh handler
            this.grid.view.un('refresh', this.grid.selModel.refresh, this.grid.selModel);
            this.grid.view.on('refresh', this.onViewRefresh, this);
            this.grid.view.on('beforeitemmousedown', function (view, record, item, index, event, eOpts) {
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

                    var isChecked = this.selected[record.internalId];
                    if (!isChecked) {
                        this.grid.selModel.select(index, true, false);
                    }
                    else {
                        this.grid.selModel.deselect(index);
                    }

                    return false;
                }

                this.clearSelectionOnRowClick(view, record, item, index, event, eOpts);
                this.cancelItemContextClickWhenSelectionIsMultiple(view, record, item, index, event, eOpts);
            }, this);
            this.grid.view.headerCt.on('headerclick', this.onHeaderClick, this);
            // add a handler to detect when the user changes the selection
            this.grid.selModel.on('select', this.onRowSelect, this);
            this.grid.selModel.on('deselect', this.onRowDeselect, this);
            this.grid.getStore().on('beforeload', function () {
                this.ignoreSelectionChanges = true;
            }, this);

            var pagingToolbar = this.grid.down('pagingtoolbar');
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
        this.onRowDeselect(this.grid.selModel, record);

        // If the deselected item is on the current page we need to programmatically deselect it.
        // First get the item object from the store (the record argument in this method is not the same object since the page has been refreshed)
        var storeRecord = this.grid.getStore().getById(record.internalId);
        this.grid.selModel.deselect(storeRecord);

        // Tell the selection model about a change.
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * Selects all the rows in the grid, including those on other pages
     * Be very careful using this on very large datasets
     */
    selectAll: function () {
        this.grid.selModel.selectAll();
    },

    /**
     * Clears selections across all pages
     */
    clearSelection: function () {
        this.selections = [];
        this.selected = {};
        this.grid.selModel.deselectAll();
        this.onViewRefresh();
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * @private
     */
    onViewRefresh: function () {
        this.ignoreSelectionChanges = true;
        // explicitly refresh the selection model
        this.grid.selModel.refresh();
        // selection changed from view updates, restore full selection
        var ds = this.grid.getStore();
        var i;
        // TODO: Optimize.
        for (i = ds.getCount() - 1; i >= 0; i--) {
            if (this.selected[ds.getAt(i).internalId]) {
                this.grid.selModel.select(i, true, false);
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
            if (!this.selected[rec.internalId]) {
                this.selections.push(rec);
                this.selected[rec.internalId] = true;
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
                this.grid.selModel.selectAll();
            }
        }

        return false;
    },

    /**
     * @private
     */
    onRowDeselect: function (rowModel, record, index, eOpts) {
        if (!this.ignoreSelectionChanges) {
            if (this.selected[record.internalId]) {
                for (var j = this.selections.length - 1; j >= 0; j--) {
                    if (this.selections[j].internalId == record.internalId) {
                        this.selections.splice(j, 1);
                        this.selected[record.internalId] = false;
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
        this.grid.selModel.fireEvent("selectionchange", this.grid.selModel, this.selections);
    },

    /**
     * @private
     */
    cancelItemContextClickWhenSelectionIsMultiple: function (view, record, item, index, event, eOpts) {
        var isRightClick = event.button === 2;
        var recordIsSelected = this.selected[record.internalId];
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
