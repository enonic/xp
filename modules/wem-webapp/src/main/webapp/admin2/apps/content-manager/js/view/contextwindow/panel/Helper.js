Ext.define('Admin.view.contentManager.contextwindow.panel.Helper', {

    constructor: function() {
    },

    statics: {

        /**
         * @param child
         * @returns {Ext_container_Container}
         */
        getContextWindowFromChildCmp: function (child) {
            return child.up('contextWindow');
        },

        /**
         * @param contextWindowCmp
         * @returns {Ext_util_Region}
         */
        getContextWindowViewRegion: function (contextWindowCmp) {
            return contextWindowCmp.getEl().getViewRegion();
        },

        /**
         * @returns {Html_dom_Element}
         */
        getLiveEditIFrameDomEl: function () {
            return Ext.DomQuery.selectNode('#live-edit-iframe');
        },

        getLiveEditIFrameViewRegion: function () {
            return Ext.fly(this.getLiveEditIFrameDomEl()).getViewRegion();
        },

        getLiveEditIFrameContainerEl: function () {
            return Ext.get('live-edit-iframe-container')
        },

        /**
         * @returns {window|Window|Window}
         */
        getLiveEditWindow: function () {
            return  this.getLiveEditIFrameDomEl().contentWindow;
        },

        /**
         * @returns JQuery
         */
        getJQueryFromLiveEditPage: function () {
            return  this.getLiveEditIFrameDomEl().contentWindow.$liveEdit;
        }
    }

});
