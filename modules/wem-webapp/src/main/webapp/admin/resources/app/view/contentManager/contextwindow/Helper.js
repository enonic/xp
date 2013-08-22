Ext.define('Admin.view.contentManager.contextwindow.Helper', {
    constructor: function() { /**/ },

    statics: {
        /**
         * NOTE:
         * This method should at a later stage in the project be moved in order to
         * share the functionality between Live Edit and Context Window.
         *
         * @param componentType {string}
         * @returns {string}
         */
        resolveComponentTypeIconCls: function (componentType) {
            var iconCls;
            switch (componentType) {
                case 'page':
                    iconCls = 'icon-file';
                    break;
                case 'region':
                    iconCls = 'icon-compass';
                    break;
                case 'layout':
                    iconCls = 'icon-columns';
                    break;
                case 'part':
                    iconCls = 'icon-puzzle-piece';
                    break;
                case 'image':
                    iconCls = 'icon-picture';
                    break;
                case 'paragraph':
                    iconCls = 'icon-edit';
                    break;
                case 'content':
                    iconCls = 'icon-file-text-alt';
                    break;
                default:
                    iconCls = '';
            }
            return iconCls;
        },

        /**
         * @param deviceType {string}
         * @returns {string}
         */
        resolveDeviceTypeIconCls: function (deviceType) {
            var iconCls;
            switch (deviceType) {
                case 'monitor':
                    iconCls = 'icon-desktop';
                    break;
                case 'monitor_full':
                    iconCls = 'icon-desktop';
                    break;
                case 'mobile':
                    iconCls = 'icon-mobile-phone';
                    break;
                case 'tablet':
                    iconCls = 'icon-tablet';
                    break;
                default:
                    iconCls = '';
            }
            return iconCls;
        }
    }

});
