Ext.define('Admin.view.SummaryTreePanel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.summaryTreePanel',
    cls: 'admin-summary-tree-panel',
    autoHeight: true,
    border: false,
    useArrows: true,
    rootVisible: false,
    multiSelect: false,
    singleExpand: false,
    dataType: '',
    showAllMode: false,
    comparisonMode: false,

    listeners: {
        afterRender: function () {
            this.createToolbar();
            this.renderBasicView();
        }
    },

    initComponent: function () {
        var self = this;

        this.store = Ext.create('Ext.data.TreeStore', {
            fields: [
                {name: 'label', type: 'string'},
                {name: 'fieldType', type: 'string'},
                {name: 'newValue', type: 'string'},
                {name: 'previousValue', type: 'string'},
                {name: 'changeType', type: 'string'}
            ]
        });

        this.dockedItems = [
            {
                xtype: 'container',
                itemId: 'admin-tree-panel-toolbar-container',
                cls: 'admin-tree-panel-toolbar-container',
                html: Templates.common.summaryToolbar
            }
        ];

        var fieldColumn = {
            xtype: 'treecolumn',
            text: 'Field',
            itemId: 'fieldColumn',
            width: 206,
            sortable: false,
            menuDisabled: true,
            dataIndex: 'label'
        };

        var newColumn = {
            text: 'New',
            itemId: 'newColumn',
            flex: 1.3,
            dataIndex: 'newValue',
            sortable: false,
            menuDisabled: true
        };

        var previousColumn = {
            text: 'Previous',
            itemId: 'previousColumn',
            hidden: true,
            flex: 7,
            dataIndex: 'previousValue',
            resizeable: false,
            sortable: false,
            menuDisabled: true
        };

        this.columns = [
            fieldColumn,
            newColumn,
            previousColumn
        ];

        this.viewConfig = {
            getRowClass: function (record, index) {
                return self.rowClassResolver(record, index);
            }
        };

        this.callParent(arguments);
    },

    rowClassResolver: function (record, index) {
        var cssClass = '';

        var changeType = record.get('changeType');
//        if (changeType === 'none') {
//            cssClass += ' hidden';
//        }
        return cssClass;
    },

    renderBasicView: function () {
        this.down('#newColumn').renderer = function (value, metaData, record, rowIndex, colIndex, store) {
            metaData.tdCls = record.get('changeType');
            return value;
        };

        this.down('#previousColumn').renderer = function (value, metaData, record, rowIndex, colIndex, store) {
            metaData.tdCls = record.get('changeType');
            return value;
        };

        this.getView().refresh();
    },

    renderComparisonView: function () {
        this.down('#newColumn').renderer = function (value, metaData, record, rowIndex, colIndex, store) {
            var changeType = record.get('changeType');
            if (changeType !== 'removed') {
                metaData.tdCls = changeType;
            }
            return value;
        };

        this.down('#previousColumn').renderer = function (value, metaData, record, rowIndex, colIndex, store) {
            var changeType = record.get('changeType');
            var fieldType = record.get('fieldType');
            var val = value;

            metaData.tdCls = changeType !== 'added' ? changeType : 'blank';

            if (fieldType === 'RelatedList') {
                val = '';
            }
            return val;
        };

        this.getView().refresh();
    },

    showPreviousColumn: function (show) {
        var previousColumn = this.down('#previousColumn');
        if (show) {
            previousColumn.show();
        } else {
            previousColumn.hide();
        }
    },

    createToolbar: function () {
        // TODO: Refactor when summary is finalized
        var toolbar = this.getDockedItems('#admin-tree-panel-toolbar-container')[0];
        toolbar.getEl().addListener('click', function (event, target) {
            var targetElement = Ext.get(target);
            if (targetElement.hasCls('admin-summary-show-all-fields-button')) {
                this.toggleShowAllView();

                var infoElement = targetElement.parent().child('span');
                infoElement.dom.innerHTML = this.showAllMode ? 'All fields are listed below' : 'Modified fields are displayed below';
                targetElement.dom.innerHTML = this.showAllMode ? 'Hide unmodified fields' : 'Show all fields';
            }

            if (targetElement.hasCls('admin-summary-show-comparison-button')) {
                this.toggleComparisonView();
                targetElement.dom.innerHTML = this.comparisonMode ? 'Hide comparison' : 'Show comparison';
            }
        }, this);

        this.getEl().addListener('click', function (event, target) {
            Ext.Msg.alert('Oops', 'Not implemented yet');
        }, this, {
            delegate: '.admin-summary-show-details-link'
        });
    },

    toggleShowAllView: function () {
        // TODO: Both TreeStore filter and NodeInterface -> show/hide are missing in ExtJs 4.x
        this.showAllMode = !this.showAllMode;
        if (this.changedData && this.initialData) {
            this.setRootNode();
        }
    },

    setDiffData: function (initialData, changedData) {
        this.initialData = initialData;
        this.changedData = changedData;
        delete this.shortenDiff;
        delete this.fullDiff;
        this.setRootNode();
    },

    setRootNode: function () {
        if (this.showAllMode) {
            this.getStore().setRootNode(this.getFullDiff());
        } else {
            this.getStore().setRootNode(this.getShortenDiff());
        }
    },

    toggleComparisonView: function () {
        if (this.comparisonMode) {
            this.showPreviousColumn(false);
            this.renderBasicView();
        } else {
            this.showPreviousColumn(true);
            this.renderComparisonView();
        }

        this.comparisonMode = !this.comparisonMode;
    },

    getShortenDiff: function () {
        if (!this.shortenDiff && this.changedData && this.initialData) {
            this.shortenDiff = Admin.plugin.Diff.compare(this.dataType, this.initialData, this.changedData, true);
        }
        return Ext.clone(this.shortenDiff || {});
    },

    getFullDiff: function () {
        if (!this.fullDiff && this.changedData && this.initialData) {
            this.fullDiff = Admin.plugin.Diff.compare(this.dataType, this.initialData, this.changedData);
        }
        return Ext.clone(this.fullDiff || {});
    }
});
