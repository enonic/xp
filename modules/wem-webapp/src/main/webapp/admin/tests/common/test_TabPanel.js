StartTest( function( t )
{
    t.requireOk(
            [
                'Admin.view.TabPanel'
            ],
            function()
            {
                var tabPanel = Ext.create( 'widget.cmsTabPanel', {
                    renderTo: Ext.getBody()
                } );
                //Create tab from request
                var requestConfig = {
                    url: 'tests/account/json/UserData.json',
                    method: 'GET',
                    createTabFromResponse: function( response )
                    {
                        var jsonObj = Ext.JSON.decode( response.responseText );
                        t.endWait( 'request' );
                        return {
                            xtype: 'panel'
                        };
                    }
                };
                t.isCalled( 'createTabFromResponse', requestConfig, 'Response function must be called' );
                tabPanel.addTab( {id: 'tab1', title: 'tab1'} );
                t.is( tabPanel.getTabCount(), 1, 'Tab Panel should contain one tab element' );

                t.ok( tabPanel.getTabById( 'tab1' ), 'Tab item with tab1 id should exist' );

                tabPanel.addTab( {id: 'tab2', title: 'tab2'} );
                t.is( tabPanel.getTabCount(), 2, 'Tab Panel should contain two tab elements' );

                tabPanel.addTab( {id: 'tab1', title: 'tab1'} );
                t.is( tabPanel.getTabCount(), 2,
                        'Tab Panel should contain two tab elements (after adding element with existing id)' );

                tabPanel.removeAllOpenTabs();
                t.is( tabPanel.getTabCount(), 1, 'Only one tab should exist after removing all tabs' );


                tabPanel.addTab( {id: 'tab3', title: 'tab3'}, 1, requestConfig );
                t.wait( 'request' );
                t.is( tabPanel.getTabCount(), 2, 'Tab Panel should contain two tabs' );

            }
    )
} );