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
        var data = contentTypeWizard.getData();
        var contentType = data.configXML;
        var iconRef = data.iconRef;
        var contentTypeParams = {
            contentType: contentType,
            iconReference: iconRef
        };

        var onUpdateContentTypeSuccess = function (created, updated) {
            if (created || updated) {
                if (closeWizard) {
                    me.getContentTypeWizardTab().close();
                }

                Admin.MessageBus.showFeedback({
                    title: 'Content Type was saved',
                    message: 'Content Type was saved',
                    opts: {}
                });

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
