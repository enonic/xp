Ext.define('Admin.controller.account.DetailPanelController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling Account Detail UI events       */

    stores: [],
    models: [],
    views: [],

    init: function () {
        this.control(
            {
                'accountDetail': {
                    afterrender: this.initDetailToolbar
                }
            }
        );
    },

    initDetailToolbar: function () {
        var accountDetail = this.getAccountDetailPanel();
        accountDetail.updateTitle(this.getPersistentGridSelectionPlugin());
        accountDetail.showNoneSelection();
    }

});
