Ext.define('Admin.view.home.AppSelector', {
    extend: 'Ext.container.Container',
    alias: 'widget.homeAppSelector',

    initComponent: function () {
        var me = this;

        console.log('init app selector container');

        /*
         Inline model and store for now
         */
        me.appsStore = null;
        me.initModelAndStore();

        me.createView();

        me.callParent(arguments);
    },


    createView: function () {
        var me = this;

        me.addSearchTextField();
        me.addAppSelectorListView();
    },


    addSearchTextField: function () {
        var me = this;

        Ext.create('Ext.form.field.Text', {
            renderTo: 'admin-home-app-selector-search-input-container',
            id: 'admin-home-app-selector-search',
            emptyText: 'Application Search',
            width: '470px',
            listeners: {
                change: function (textField, newValue, oldValue) {
                    var appsStore = me.appsStore;
                    appsStore.clearFilter();
                    appsStore.filter('name', newValue);
                }
            }
        });
    },


    addAppSelectorListView: function () {
        var me = this;

        Ext.create('Ext.view.View', {
            store: me.appsStore,
            tpl: Templates.home.appTile,
            renderTo: 'admin-home-app-tiles-placeholder',
            trackOver: true,
            overItemCls: 'admin-home-app-tile-over',
            itemSelector: 'div.admin-home-app-tile',
            emptyText: 'No application found',
            listeners: {
                itemmouseenter: function (view, record, item, index, evt, eOpts) {
                    var data = record.data;
                    me.updateAppInfo(data.name, data.description);
                },
                itemmouseleave: function (view, record, item, index, evt, eOpts) {
                    me.updateAppInfo('', '');
                },
                itemclick: function (view, record, item, index, evt, eOpts) {
                    //document.location.href = record.data.appUrl;
                    alert('go to: ' + record.data.appUrl);
                }
            }
        });
    },


    updateAppInfo: function (title, description) {
        Ext.fly('admin-home-app-info-name').setHTML(title);
        Ext.fly('admin-home-app-info-description').setHTML(description);
    },


    initModelAndStore: function () {
        var me = this;

        Ext.define('Admin.model.home.AppsModel', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'id', type: 'string' },
                { name: 'name', type: 'string' },
                { name: 'description', type: 'string' },
                { name: 'appUrl', type: 'string' },
                { name: 'icon', type: 'string' }
            ]
        });

        me.appsStore = Ext.create('Ext.data.Store', {
            id: 'appsStore',
            model: 'Admin.model.home.AppsModel',
            pageSize: 100,
            data: [
                {
                    id: 'app-0',
                    name: 'Content Manager',
                    description: 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.',
                    appUrl: 'app-content-manager.jsp',
                    icon: 'resources/images/icons/metro/48x48/data.png'
                },
                {
                    id: 'app-1',
                    name: 'Space Admin',
                    description: 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.',
                    appUrl: 'blank.html',
                    icon: 'resources/images/icons/metro/48x48/earth.png'
                },
                {
                    id: 'app-2',
                    name: 'Content Studio',
                    description: 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.',
                    appUrl: 'app-content-studio.jsp',
                    icon: 'resources/images/icons/metro/48x48/cube_molecule.png'
                },
                {
                    id: 'app-3',
                    name: 'Cluster',
                    description: 'Ei malis impedit expetendis quo. His id iusto nihil quando, qui facer equidem molestie ei, dolore possit eripuit ad eum. Dissentiet instructior no nec, blandit salutandi ea vel, legere essent quo at. At eos consul perpetua. Sea duis postea et, cum agam justo cu. Nulla numquam vim no.',
                    appUrl: 'blank.html',
                    icon: 'resources/images/icons/metro/48x48/virus.png'
                },
                {
                    id: 'app-4',
                    name: 'Userstores',
                    description: 'Ex vis fugit euripidis dissentias, id impedit suavitate mediocritatem sea. Cu error apeirian his. Qui te possit accumsan vituperata, facete prodesset comprehensam ei sea. Odio recteque sententiae an mei. Per meliore deleniti in, ex probo luptatum indoctum nec.',
                    appUrl: 'app-userstore.jsp',
                    icon: 'resources/images/icons/metro/48x48/user_earth.png'
                },
                {
                    id: 'app-5',
                    name: 'Accounts',
                    description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
                    appUrl: 'app-account.jsp',
                    icon: 'resources/images/icons/metro/48x48/users2.png'
                },
                {
                    id: 'app-6',
                    name: 'Scheduler',
                    description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
                    appUrl: 'blank.html',
                    icon: 'resources/images/icons/metro/48x48/clock.png'
                },
                {
                    id: 'app-7',
                    name: 'Live Portal Trace',
                    description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
                    appUrl: 'blank.html',
                    icon: 'resources/images/icons/metro/48x48/window_time.png'
                }

            ]
        });
    }

});
