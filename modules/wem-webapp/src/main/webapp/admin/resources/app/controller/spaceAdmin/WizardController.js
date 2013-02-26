Ext.define('Admin.controller.spaceAdmin.WizardController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'spaceAdminWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'spaceAdminWizardPanel *[action=saveSpace]': {
                click: function () {
                    alert('Not implemented');
                }
            },
            'spaceAdminWizardPanel *[action=deleteSpace]': {
                click: function () {
                    alert('Not implemented');
                }
            }
        });
    },

    closeWizard: function (el, e) {
        var tab = this.getWizardTab();
        var spaceWizard = this.getWizardPanel();
        if (spaceWizard.getWizardPanel().isWizardDirty) {
            Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                function (answer) {
                    if ('yes' === answer) {
                        tab.close();
                    }
                });
        } else {
            tab.close();
        }
    },

    /*      Getters     */

    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getWizardPanel: function () {
        return this.getWizardTab();
    }

})
;
