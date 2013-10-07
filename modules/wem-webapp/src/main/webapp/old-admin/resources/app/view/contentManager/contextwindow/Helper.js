Ext.define('Admin.view.contentManager.contextwindow.Helper', {
    constructor: function() { /**/ },

    statics: {
        /**
         * NOTE:
         * This method should at a later stage in the project be moved in order to
         * share the functionality between Live Edit and Context Window.
         *
         * @param componentTypeName {number}
         * @returns {string}
        */
        resolveComponentTypeIconCls: function (componentTypeName) {
            var iconCls;

            switch (componentTypeName) {
                case 'page':
                    iconCls = 'live-edit-font-icon-page';
                    break;
                case 'region':
                    iconCls = 'live-edit-font-icon-region';
                    break;
                case 'layout':
                    iconCls = 'live-edit-font-icon-layout';
                    break;
                case 'part':
                    iconCls = 'live-edit-font-icon-part';
                    break;
                case 'image':
                    iconCls = 'live-edit-font-icon-image';
                    break;
                case 'paragraph':
                    iconCls = 'live-edit-font-icon-paragraph';
                    break;
                case 'content':
                    iconCls = 'live-edit-font-icon-content';
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
