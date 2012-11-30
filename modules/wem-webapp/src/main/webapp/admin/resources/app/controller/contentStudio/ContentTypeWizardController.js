Ext.define('Admin.controller.contentStudio.ContentTypeWizardController', {
    extend: 'Admin.controller.contentStudio.ContentTypeController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'contentStudioWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentStudioWizardPanel *[action=saveContentType]': {
                click: function (el, e) {
                    me.saveContentType(el.up('contentStudioWizardPanel'), false);
                }
            },
            'contentStudioWizardPanel wizardPanel': {
                finished: function (wizard, data) {
                    me.saveContentType(wizard.up('contentStudioWizardPanel'), true);
                }
            },
            'contentWizardToolbar *[action=deleteContentType]': {
                click: function (el, e) {
                    this.deleteContentType(this.getContentTypeWizardPanel().data);
                }
            }
        });
    },

    closeWizard: function (el, e) {
        var tab = this.getContentTypeWizardTab();
        var contentTypeWizard = this.getContentTypeWizardPanel();
        if (contentTypeWizard.getWizardPanel().isWizardDirty) {
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

    saveContentType: function (contentTypeWizard, closeWizard) {
        var me = this;
        var contentType = contentTypeWizard.getData().configXML;
        var contentTypeParams = {
            contentType: contentType
        };

        var parentApp = parent.mainApp;
        var onUpdateContentTypeSuccess = function (created, updated) {
            if (created || updated) {
                if (closeWizard) {
                    me.getContentTypeWizardTab().close();
                }
                if (parentApp) {
                    parentApp.fireEvent('notifier.show', "Content Type was saved",
                        "Content Type was saved", false);
                }
                me.getTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateContentType(contentTypeParams, onUpdateContentTypeSuccess);
    },

    /*      Getters     */

    getContentTypeWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentTypeWizardPanel: function () {
        return this.getContentTypeWizardTab();
    }

});
