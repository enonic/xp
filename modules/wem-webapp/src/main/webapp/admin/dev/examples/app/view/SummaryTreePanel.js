Ext.define('Admin.view.SummaryTreePanel', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.summaryTreePanel',
    cls: 'admin-summary-tree-panel',
    autoHeight: true,
    border: false,
    useArrows: true,
    rootVisible:  false,
    multiSelect: false,
    singleExpand: false,

    showAllMode: false,
    comparisonMode: false,

    listeners: {
        afterRender: function() {
            this.initToolbar();
        }
    },

    initComponent: function() {
        var self = this;

        this.store = Ext.create('Ext.data.TreeStore', {
            fields:[
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
            flex: 2,
            dataIndex: 'newValue',
            sortable: false,
            menuDisabled: true
        };

        var previousColumn = {
            text: 'Previous',
            itemId: 'previousColumn',
            hidden: true,
            flex: 2,
            dataIndex: 'previousValue',
            sortable: false,
            menuDisabled: true
        };

        this.columns = [
            fieldColumn,
            newColumn,
            previousColumn
        ];

        this.callParent(arguments);
    },

    renderBasicView: function()
    {
        this.down('#newColumn').renderer = function ( value, metaData, record, rowIndex, colIndex, store ) {
            metaData.tdCls = record.get('changeType');
            return value;
        };

        this.down('#previousColumn').renderer = function ( value, metaData, record, rowIndex, colIndex, store ) {
            metaData.tdCls = record.get('changeType');
            return value;
        };

        this.getView().refresh();
    },

    renderComparisonView: function()
    {
        this.down('#newColumn').renderer = function ( value, metaData, record, rowIndex, colIndex, store ) {
            var changeType = record.get('changeType');
            if ( changeType !== 'removed' )
            {
                metaData.tdCls = changeType;
            }
            return value;
        };

        this.down('#previousColumn').renderer = function ( value, metaData, record, rowIndex, colIndex, store ) {
            var changeType = record.get('changeType');
            var fieldType = record.get('fieldType');
            var val = value;

            metaData.tdCls = changeType !== 'added' ? changeType : 'blank';

            if ( fieldType === 'RelatedList' )
            {
                val = '';
            }
            return val;
        };

        this.getView().refresh();
    },

    showPreviousColumn: function ( show )
    {
        var previousColumn = this.down( '#previousColumn' );
        if ( show )
        {
            previousColumn.show();
        }
        else
        {
            previousColumn.hide();
        }
    },

    initToolbar: function()
    {
        // TODO: Refactor when summary is finalized
        var toolbar = this.getDockedItems('#admin-tree-panel-toolbar-container')[0];
        toolbar.getEl().addListener('click', function( event, target ) {
            var targetElement = Ext.get( target );
            if ( targetElement.hasCls( 'admin-summary-show-all-fields-button' ) )
            {

                this.toggleShowAllView();

                var infoElement = targetElement.parent().child('span');
                infoElement.dom.innerHTML = this.showAllMode ? 'All fields are listed below' : 'Modified fields are displayed below';
                targetElement.dom.innerHTML = this.showAllMode ? 'Hide unmodified fields' : 'Show all fields';
            }

            if ( targetElement.hasCls( 'admin-summary-show-comparison-button' ) )
            {
                this.toggleComparisonView();
                targetElement.dom.innerHTML = this.comparisonMode ? 'Hide comparison' : 'Show comparison';
            }
        }, this);
    },

    toggleShowAllView: function()
    {
        // TODO: Both TreeStore filter and NodeInterface -> show/hide are missing in ExtJs 4.x
        this.showAllMode = !this.showAllMode;
    },

    toggleComparisonView: function()
    {
        if ( this.comparisonMode )
        {
            this.showPreviousColumn( false );
            this.renderBasicView();
        }
        else
        {
            this.showPreviousColumn( true );
            this.renderComparisonView();
        }

        this.comparisonMode = !this.comparisonMode;
    }
});
