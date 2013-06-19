module app_ui {

    export class BrowseToolbar extends api_ui_toolbar.Toolbar {
        private isLiveMode:bool = false;

        constructor() {
            super();
            super.addAction(app.ContentActions.NEW_CONTENT);
            super.addAction(app.ContentActions.EDIT_CONTENT);
            super.addAction(app.ContentActions.OPEN_CONTENT);
            super.addAction(app.ContentActions.DELETE_CONTENT);
            super.addAction(app.ContentActions.DUPLICATE_CONTENT);
            super.addAction(app.ContentActions.MOVE_CONTENT);
            super.addGreedySpacer();

            // TODO add widget component for preview toggle
            var previewToggle = new api_dom.Element('span', 'preview-toggle');
            previewToggle.getEl().setInnerHtml('TODO preview-toggle');

            super.addAction(app.ContentActions.BROWSE_CONTENT_SETTINGS);
            super.addElement(previewToggle);
        }
    }
}

/*
 Ext.define('Admin.view.contentManager.BrowseToolbar', {
 extend: 'Ext.toolbar.Toolbar',
 alias: 'widget.browseToolbar',

 requires: [
 'Ext.ux.toggleslide.ToggleSlide'
 ],

 cls: 'admin-toolbar',
 border: true,

 defaults: {
 scale: 'medium',
 iconAlign: 'top',
 minWidth: 64
 },

 initComponent: function () {
 //Handlers for this items put in the Admin.controller.contentManager.Controller
 this.items = <any[]>[

 {
 text: ' New',
 disabled: true,
 action: 'newContent'
 },
 {
 text: 'Edit',
 action: 'editContent'
 },
 {
 text: 'Open',
 action: 'viewContent'
 },
 {
 text: 'Delete',
 action: 'deleteContent'
 },
 {
 text: 'Duplicate',
 action: 'duplicateContent'
 },
 {
 text: 'Move',
 disabled: true,
 action: 'moveContent'
 },
 '->',
 {
 xtype: 'toggleslide',
 onText: 'Preview',
 offText: 'Details',
 action: 'toggleLive',
 state: this.isLiveMode
 },
 {
 iconCls: 'icon-toolbar-settings',
 action: 'showToolbarMenu',
 minWidth: 42,
 padding: '6 8 6 12'
 }
 ];

 this.callParent(arguments);
 }

 });
 */