var titles = [
    'Tutorial: Creating a reg ex parser with Scala',
    'Consuming XML with Backbone.js',
    'How to build a firebug extension - Part 1 (getting started)',
    'HTML5 video',
    'How to diff various Enonic CMS installations',
    'Microdata - semantic markup - schema.org',
    'Creating a contenttype editor with Ext JS',
    'Real-time indexing of events with ElasticSearch',
    'Live Portal Trace for Enonic CMS',
    'Designing a Usability Blueprint for a New Admin Console'
];


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

                            html: '<h2>Sortable</h2><p>General ExtJS sortable that makes any ExtJS components sortable</p>' +
                                  '<p>Example:</p>' +
                                  '<pre>' +
                                  'var sortable = new Admin.lib.Sortable(parentComponent, [config])</pre>' +
                                  '<p style="margin-top:10px !important;">Parameters:</p>' +
                                  '<pre style="margin-top:10px">' +
                                  'parentComponent: <a href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.Component">{Component}</a> The parent Ext component for the sortables\n' +
                                  'config: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/Object">{Object}</a> {\n' +
                                  // '\tgroup: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A named drag drop group to which this object belongs\n' +
                                  '\thandle: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A CSS query <a href="http://www.w3.org/TR/selectors-api/">selector string</a> to element(s) within the sortable component that should be used for dragging. TBD\n' +
                                  '\tproxyHtml: <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/String">{String}</a> A HTML string describing a drag proxy. If not set the source element will be cloned' +
                                  '\n}</pre>' +
                                  '<p>Note: The sortables must be rendered before creating the sortable</p>'
                        },
                        {
                            region: 'center',
                            id: 'center',
                            autoScroll: true,
                            padding: '0 30',
                            border: false,
                            defaults: {
                                margin: '1 0'
                            },
                            layout: {
                                type: 'table',
                                columns: 2,
                                tdAttrs: {
                                    valign: 'top',
                                    width: 530,
                                    style: 'padding: 0 70px 0 0'
                                }
                            },
                            items: [
                                {
                                    xtype: 'container',
                                    items: [
                                        {
                                            xtype: 'component',
                                            html: '<h3>Demo</h3><p>No handle, no drag proxy</p>'
                                        },
                                        {
                                            xtype: 'container',
                                            listeners: {
                                                render: function (component) {
                                                    for (var i = 0; i < titles.length; i++) {
                                                        component.add(createTestContainer(i));
                                                    }

                                                    new Admin.lib.Sortable(component);
                                                }
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'container',
                                    items: [
                                        {
                                            xtype: 'component',
                                            html: '<h3>Forms</h3><p>With drag handle and drag proxy</p>'
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

                                                    new Admin.lib.Sortable(component, {
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
        html: '<div>[' + counter + '] ' + titles[counter] + '</div>'
    };
}


function createTestFormContainer(counter) {
    return {
        xtype: 'fieldcontainer',
        cls: 'admin-sortable',
        items: [
            {
                xtype: 'component',
                html: '<div class="admin-dragger">#</div>'
            },
            {
                xtype: 'component',
                html: '<div><b>Block '+counter+'</b></div>'
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
