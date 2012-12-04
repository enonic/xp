Ext.application({
    name: 'App',

    controllers: [
    ],

    requires: [
    ],

    /**
     *
     * @param a
     */
    launch: function (a) {

        Ext.create('Ext.container.Viewport', {
            padding: 0,
            defaults: {
                margin: 0
            },

            layout: 'fit',
            items: [
                {
                    layout: 'border',
                    bodyStyle: 'background-color: #fff',
                    defaults: {
                        border: false,
                        padding: '10 30'
                    },
                    items: [
                        {
                            region: 'north',
                            xtype: 'component',
                            padding: '10 30',

                            html: '<h2>Sortable</h2><p>ExtJS utility that makes a set of Ext components sortable<br/>Any added child component will automatically be sortable</p>' +
                                  '<p>Usage:</p>' +
                                  '<pre>var sortable = new Admin.lib.Sortable(parentComponent, group, [config])</pre>' +
                                  '<p style="margin-top:10px !important;">Parameters:</p>' +
                                  '<pre style="margin-top:10px">' +
                                  'parentComponent: <a href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.Component">{Component}</a> The parent Ext component for the sortables\n' +
                                  'group: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A named drag drop group to which this object belongs\n' +
                                  'config: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/Object">{Object}</a> {\n' +
                                  '\thandle: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A CSS query <a href="http://www.w3.org/TR/selectors-api/">selector string</a> to element(s) within the sortable component that should initiate the drag. TBD\n' +
                                  '\tproxyHtml: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A HTML string describing a drag proxy. If not set the source element will be cloned' +
                                  '\n}</pre><p></p>'
                        },
                        {
                            region: 'center',
                            id: 'center',
                            autoScroll: true,
                            padding: '10 30',
                            border: false,
                            defaults: {
                                margin: '1 0'
                            },
                            items: [
                                {
                                    xtype: 'component',
                                    html: '<h3>Demo</h3><p>Plain</p>'
                                }
                            ],
                            listeners: {
                                render: function (component) {
                                    for (var i = 0; i < 10; i++) {
                                        component.add(createTestContainer(i));
                                    }

                                    new Admin.lib.Sortable(component, 'group1');
                                }
                            }
                        },
                        {
                            region: 'east',
                            xtype: 'form',
                            flex: 1,
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'component',
                                    html: '<h3>Forms</h3><p>With a drag handle and a drag proxy</p>'
                                },
                                {
                                    // Fieldset in Column 1 - collapsible via toggle button
                                    xtype:'fieldset',
                                    columnWidth: 0.5,
                                    title: 'Fieldset 1',

                                    listeners: {
                                        render: function (component) {
                                            for (var i = 0; i < 3; i++) {
                                                component.add(createTestFormContainer(i));
                                            }

                                            new Admin.lib.Sortable(component, 'group2', {
                                                proxyHtml: '<div><img src="../../admin/resources/images/icons/128x128/form_blue.png"/></div>',
                                                handle: '.admin-dragger'
                                            });
                                        }
                                    },

                                    items :[

                                    ]
                                }

                            ]

                        }
                    ]
                }
            ]
        });

    }
});


function createTestContainer(counter) {
    return {
        xtype: 'container',
        cls: 'admin-sortable',
        style: 'cursor: move',
        padding: '5 0',
        html: '<h5>Tutorial: Creating a reg ex parser with Scala [' +
              counter + ']</h5>'
    };
}
function createTestFormContainer(counter) {
    return {
        xtype: 'fieldcontainer',
        cls: 'admin-sortable',
        items: [
            {
                xtype: 'component',
                html: '<div class="admin-dragger">drag handle</div>'
            },
            {
                xtype: 'component',
                html: '<h5>Form '+counter+'</h5>'
            },

            {
                xtype: 'textfield',
                fieldLabel: 'Text ' + counter
            },
            {
                xtype: 'htmleditor',
                fieldLabel: 'HTML '  + counter
            }
        ]

    };
}
