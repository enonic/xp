Ext.define('Admin.controller.contentManager.ContentWizardController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Controller for handling Content Wizard UI events       */

    stores: [
    ],
    models: [
    ],
    views: [],

    EMPTY_DISPLAY_NAME_TEXT: 'Display Name',

    init: function () {
        var me = this;
        me.control({
            'contentWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentWizardPanel *[action=saveContent]': {
                click: function (el, e) {
                    me.saveContent(el.up('contentWizardPanel'), false);
                }
            },
            'contentWizardPanel wizardPanel': {
                finished: function (wizard, data) {
                    me.saveContent(wizard.up('contentWizardPanel'), true);
                }
            },
            'contentWizardToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    me.duplicateContent(this.getContentWizardPanel().data);
                }
            },
            'contentWizardToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent(this.getContentWizardPanel().data);
                }
            },
            'contentWizardToolbar *[action=cycleMode]': {
                click: this.cycleMode
            }
        });

        me.application.on({
        });
    },

    cycleMode: function (el, e) {
        var mode = this.getContentWizardPanel().cycleLiveEdit();
        var text, cls;
        switch (mode) {
        case 0:
            // form
            text = 'Form Edit';
            cls = 'icon-keyboard-key-24';
            break;
        case 1:
            // split
            text = 'Split Edit';
            cls = 'icon-split-hor-24';
            break;
        case 2:
            // live
            text = 'Live Edit';
            cls = 'icon-monitor-24';
            break;
        }
        el.setText(text);
        el.setIconCls(cls);
    },

    closeWizard: function (el, e) {
        var tab = this.getContentWizardTab();
        var contentWizard = this.getContentWizardPanel();
        if (contentWizard.getWizardPanel().isWizardDirty) {
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

    saveContent: function (contentWizard, closeWizard) {
        var me = this;
        var contentType = contentWizard.data.contentType;
        var content = contentWizard.data.content;
        var contentParent = contentWizard.data.contentParent;

        var contentWizardData = contentWizard.getData();
        var contentData = contentWizardData.contentData;
        var displayName = contentWizardData.displayName;
        var isNewContent = !content.path;

        var contentParams = {
            contentData: contentData,
            qualifiedContentTypeName: contentType.qualifiedName,
            contentPath: isNewContent? null : content.path,
            parentContentPath: isNewContent? contentParent.path : null,
            displayName: displayName
        };

        var onUpdateContentSuccess = function (created, updated, contentPath) {
            if (created) {
                content.path = contentPath;
            }
            if (created || updated) {
                if (closeWizard) {
                    me.getContentWizardTab().close();
                }

                Admin.MessageBus.showFeedback({
                    title: 'Content was saved',
                    message: 'Content with path: ' + contentParams.contentPath + ' was saved',
                    opts: {}
                });

                me.getContentTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateContent(contentParams, onUpdateContentSuccess);
    },


    /*      Getters     */

    getContentWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentWizardPanel: function () {
        return this.getContentWizardTab().down('contentWizardPanel');
    }

});
