Ext.define('Admin.view.account.AccountKeyMap', {
    extend: 'Ext.util.KeyMap',

    disableOnMask: true,

    constructor: function (actionHandlers) {
        var me = this;
        var document = Ext.getDoc();
        me.callParent([document, [
            {
                key: "n",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.newMegaMenu
            },
            {
                key: "o",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.openItem
            },
            {
                key: "e",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.editItem
            },
            {
                key: "s",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.saveItem
            },
            {
                key: "j",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.prevStep
            },
            {
                key: "k",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.nextStep
            },
            {
                key: Ext.EventObject.DELETE,
                ctrl: false,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.deleteItem
            }
        ]]);
    },

    checkModifiers: function (binding, e) {
        var isMasked = false;
        if (this.disableOnMask) {
            isMasked = Ext.getDoc().select('body.x-body-masked').getCount() > 0;
        }
        return !isMasked && this.callParent(arguments);
    }

});