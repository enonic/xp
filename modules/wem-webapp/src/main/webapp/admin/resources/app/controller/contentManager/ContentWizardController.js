Ext.define('Admin.controller.contentManager.ContentWizardController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Controller for handling Content Wizard UI events       */

    stores: [
    ],
    models: [
    ],
    views: [
        'Admin.view.contentManager.wizard.ContentWizardPanel'
    ],

    init: function () {
        var me = this;
        me.control({
            'contentLiveEditPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentLiveEditPanel *[action=saveContent]': {
                click: function (el, e) {
                    me.saveContent(this.getContentWizardPanel(), false);
                }
            },
            'contentLiveEditPanel *[action=previewContent]': {
                click: function (el, e) {
                    me.previewContent();
                }
            },
            'contentWizardPanel *[action=publishContent]': {
                click: function (el, e) {
                    me.publishContent(this.getContentWizardPanel(), true);
                }
            },
            'contentWizardToolbar *[action=publishContent]': {
                click: function (el, e) {
                    me.publishContent(this.getContentWizardPanel(), false);
                }
            },
            'contentWizardPanel': {
                finished: function (wizard, data) {
                    me.saveContent(this.getContentWizardPanel(), true);
                }
            },
            'contentWizardToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    me.duplicateContent(this.getContentWizardTab().data);
                }
            },
            'contentWizardToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent(this.getContentWizardTab().data);
                }
            },
            'contentWizardToolbar toggleslide': {
                change: this.toggleLiveWizard
            },
            'contentWizardPanel wizardHeader': {
                displaynamechange: this.onDisplayNameChanged,
                displaynameoverride: this.onDisplayNameOverriden,
                scope: this
            },
            'contentWizardPanel *[displayNameSource]': {
                change: this.onDisplayNameSourceChanged
            }
        });

        me.application.on({});
    },

    onDisplayNameChanged: function (newName, oldName) {
        this.getTopBar().setTitleButtonText(newName);
    },

    onDisplayNameOverriden: function (overriden) {
        var wizard = this.getContentWizardPanel();
        wizard.evaluateDisplayName = wizard.isNewContent() && !overriden;
    },

    onDisplayNameSourceChanged: function (field, event, opts) {
        var wizard = this.getContentWizardPanel();
        var evaluateFn = wizard.data && wizard.data.contentType && wizard.data.contentType.contentDisplayNameScript;

        if (wizard.evaluateDisplayName && !Ext.isEmpty(evaluateFn)) {

            var rawData = wizard.getData().contentData;
            var contentData = {};
            var key;

            for (key in rawData) {
                if (rawData.hasOwnProperty(key)) {
                    contentData[key.replace(/\[0\]/g, '')] = rawData[key];
                }
            }

            var displayName = window.evaluateContentDisplayNameScript(evaluateFn, contentData);
            wizard.getWizardHeader().setDisplayName(displayName);
        }
    },

    toggleLiveWizard: function (enabled) {
        this.getContentLiveEditPanel().toggleLive();
    },

    closeWizard: function (el, e) {
        var tab = this.getContentWizardTab();
        var contentWizard = this.getContentWizardPanel();
        if (contentWizard.isWizardDirty) {
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
        var contentName = contentWizardData.name;
        var isNewContent = !content.path;

        var contentParams = {
            contentData: contentData,
            qualifiedContentTypeName: contentType.qualifiedName,
            contentId: isNewContent ? null : content.id,
            contentPath: isNewContent ? null : content.path,
            contentName: contentName,
            parentContentPath: isNewContent ? contentParent.path : null,
            displayName: displayName
        };

        var onUpdateContentSuccess = function (created, updated, contentPath, contentId) {
            if (contentPath) {
                content.path = contentPath;
            }
            if (contentId) {
                content.id = contentId;
            }
            if (created || updated) {
                if (closeWizard) {
                    me.getContentWizardTab().close();
                }

                var path = contentParams.contentPath || contentPath;

                Admin.MessageBus.showGeneral(
                    path,
                    function () {
                        alert('publish link callback');
                    },
                    function () {
                        alert('close link callback');
                    }
                );

                if (Ext.isFunction(contentWizard.washDirtyForms)) {
                    contentWizard.washDirtyForms();
                }

                me.loadContentAndFacets();
            }
        };
        this.remoteCreateOrUpdateContent(contentParams, onUpdateContentSuccess);
    },


    previewContent: function () {
        console.log("previewing content");
        window.open(Admin.lib.UriHelper.getAbsoluteUri('/dev/live-edit/page/bootstrap.jsp'));
    },


    publishContent: function (contentWizard, closeWizard) {
        var me = this;
        var contentPath = contentWizard.data.content.path;
        if (closeWizard) {
            me.getContentWizardTab().close();
        }
        Admin.MessageBus.showPublish(
            contentPath,
            function () {
                alert('result link callback');
            },
            function () {
                alert('publish link callback');
            }
        );
    },


    /*      Getters     */

    getContentWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentWizardPanel: function () {
        return this.getContentWizardTab().down('contentWizardPanel');
    },

    getContentLiveEditPanel: function () {
        return this.getContentWizardTab().down('contentLiveEditPanel');
    }

});
