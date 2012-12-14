Ext.define('App.controller.ActivityStreamController', {
    extend: 'Ext.app.Controller',

    models: ['ActivityStreamModel'],
    stores: ['ActivityStreamStore'],
    views: ['ActivityStreamPanel'],

    init: function () {
        this.control(
            {
                'activityStreamPanel': {
                    'afterrender': this.afterPanelRender
                }
            }
        );
    },

    afterPanelRender: function () {
        this.appendSpeakOutPanel();
        this.createDataView();
    },

    createDataView: function () {
        var store = this.getStore('ActivityStreamStore');
        var template = new Ext.XTemplate(Templates.main.activityStream);

        Ext.create('Ext.view.View', {
            store: store,
            tpl: template,
            loadMask: false,
            itemSelector: 'div.admin-activity-stream-message',
            renderTo: 'admin-activity-stream-messages-container',
            emptyText: 'No messages',
            listeners: {
                'itemmouseenter': {
                    fn: this.onMessageMouseEnter
                },
                'itemmouseleave': {
                    fn: this.onMessageMouseLeave
                },
                'itemclick': {
                    fn: this.onMessageClick,
                    scope: this
                }
            }
        });
    },

    onMessageMouseEnter: function (view, record, item, index, event, eOpts) {
        var dom = Ext.DomQuery;
        var favorite = dom.selectNode('.favorite', item);
        var comment = dom.selectNode('.comment', item);
        var more = dom.selectNode('.more', item);

        favorite.style.visibility = 'visible';
        comment.style.visibility = 'visible';
        more.style.visibility = 'visible';
    },

    onMessageMouseLeave: function (view, record, item, index, event, eOpts) {
        var dom = Ext.DomQuery;
        var favorite = dom.selectNode('.favorite', item);
        var comment = dom.selectNode('.comment', item);
        var more = dom.selectNode('.more', item);

        var messageIsFavorited = favorite.className.indexOf('favorited') === -1;
        if (messageIsFavorited) {
            favorite.style.visibility = 'hidden';
        }

        comment.style.visibility = 'hidden';
        more.style.visibility = 'hidden';
    },

    onMessageClick: function (view, record, item, index, event, eOpts) {
        var targetElement = new Ext.Element(event.target);

        if (targetElement.hasCls('favorite')) {
            if (targetElement.hasCls('favorited')) {
                targetElement.removeCls('favorited');
            } else {
                targetElement.addCls('favorited');
            }
        }
    },

    appendSpeakOutPanel: function () {
        var template = new Ext.XTemplate(Templates.main.speakOutPanel);
        var container = Ext.DomQuery.select('#admin-activity-stream-speak-out-panel-container')[0];
        template.append(container, {});

        this.appendSpeakOutTextField();
        this.appendUrlShortenerButton();
        this.appendSpeakOutSendButton();
    },

    appendSpeakOutTextField: function () {
        new Ext.form.field.Text(
            {
                itemId: 'speakOutTextField',
                renderTo: 'activity-stream-speak-out-text-input',
                enforceMaxLength: true,
                maxLength: 140,
                width: 247,
                enableKeyEvents: true,
                listeners: {
                    'keyup': {
                        fn: this.onSpeakOutTextFieldKeyUp,
                        scope: this
                    }
                }
            }
        );
    },

    appendUrlShortenerButton: function () {
        new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-url-shortener-button-container',
                iconCls: 'icon-link',
                handler: function () {
                }
            }
        );
    },

    appendSpeakOutSendButton: function () {
        var me = this;

        new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-send-button-container',
                text: 'Send',
                handler: function () {
                    me.postMessage(me.getSpeakOutTextField().getValue());
                    me.resetSpeakOutTextField();
                }
            }
        );
    },

    postMessage: function (message) {
        if (message.length === 0) {
            return;
        }

        var store = this.getStore('ActivityStreamStore');
        store.insert(0, [
            {
                "displayName": "Morten Eriksen",
                "photo": "resources/images/x-user.png",
                "location": "Admin",
                "action": "Said",
                "description": message,
                "prettyDate": "just now",
                "birthday": false
            }
        ]);
    },

    updateLettersLeftContainer: function () {
        var speakOutTextField = this.getSpeakOutTextField();
        var lettersLeftContainer = Ext.DomQuery.select('#activity-stream-speak-out-letters-left-container')[0];
        var textFieldMaxTextLength = speakOutTextField.maxLength;
        var textFieldTextLength = speakOutTextField.getValue().length;

        if (textFieldTextLength <= textFieldMaxTextLength) {
            lettersLeftContainer.innerHTML = String((textFieldMaxTextLength - textFieldTextLength));
        }
    },

    onSpeakOutTextFieldKeyUp: function (textField, event, eOpts) {
        var isEnterKey = event.button === 12;
        if (isEnterKey) {
            this.postMessage(textField.getValue());
            this.resetSpeakOutTextField();
        }

        this.updateLettersLeftContainer();
    },

    resetSpeakOutTextField: function () {
        var textField = this.getSpeakOutTextField();
        textField.setValue('');
        textField.focus();
    },

    getSpeakOutTextField: function () {
        return Ext.ComponentQuery.query('textfield[itemId=speakOutTextField]')[0];
    }

});
