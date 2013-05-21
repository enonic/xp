Ext.define('App.view.LoggedInUserButton', {
    extend: 'Ext.Button',
    alias: 'widget.loggedInUserButton',
    text: 'No Name',
    menu: [],
    enableToggle: true,

    constructor: function (config) {
        config = config || {};
        config.listeners = config.listeners || {};

        Ext.applyIf(config.listeners, {
            move: {scope: this, fn: function (component, x, y, options) {
                this.updatePopupPosition(component, x, y, options);
            }}
        });

        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.constructor.apply(this, arguments);
    },

    onRender: function () {
        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.onRender.apply(this, arguments);

        this.createPopup();
    },

    toggleHandler: function (button, state) {
        state ? this.popup.show() : this.popup.hide();

        if (state) {
            var changeUserInput = Ext.get(Ext.DomQuery.selectNode('#main-change-user-input'));
            changeUserInput.dom.focus();
        }
    },

    createPopup: function () {
        var user = {
            uid: '2fcab58712467eab4004583eb8fb7f89',
            displayName: 'Morten Eriksen',
            qualifiedName: 'local\\mer',
            email: 'mer@enonic.com'
        };

        this.popup = Ext.create('Ext.Component', {
            width: 320,
            floating: true,
            cls: 'admin-logged-in-user-popup',
            bodyCls: 'admin-logged-in-user-popup-body',
            renderTo: Ext.getBody()
        });
        this.popup.hide();

        this.createPopupTemplate().overwrite(this.popup.getEl(), user);
        this.updatePopupPosition();
    },

    createPopupTemplate: function () {
        var Templates_main_loggedInUserButtonPopup =
    		'<div class="admin-logged-in-user-popup-left">' +
    		    '<img src="resources/images/x-user.png"/>' +
    		'</div>' +
    		'<div class="admin-logged-in-user-popup-right">' +
    		    '<h1>{displayName}</h1>' +
    		    '<p>{qualifiedName}</p>' +
    		    '<p>{email}</p>' +
    		    '<p>&nbsp;</p>' +
    		    '<p><form action="main.html"> Change user: <input id="main-change-user-input" name="qname" type="text"/><input type="submit" style="display:none" /></form></p>' +
    		    '<p>&nbsp;</p>' +
    		    '<p><span class="link">Edit Account</span></p>' +
    		    '<p><span class="link">Change Password</span></p>' +
    		    '<p class="admin-logged-in-user-popup-log-out" style="float:right"><a href="index.html">Log Out</a></p>' +
    		'</div>';

        return new Ext.XTemplate( Templates_main_loggedInUserButtonPopup );
    },

    updatePopupPosition: function () {
        var buttonArea = this.getEl().getPageBox();
        var popupX = buttonArea.right - this.popup.width;
        var popupY = buttonArea.bottom;
        this.popup.setPosition(popupX, popupY);
    }

});

