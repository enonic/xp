
Ext.define( 'Admin.plugin.Diff', {

    singleton: true,

    defaultUserStore: 'default',

    userFieldsets: {
        'profile': [
            'firstName', 'middleName', 'lastName', 'organization', 'homePage', 'fax', 'mobile', 'phone'
        ],
        'user': [
            'username', 'email', 'password', 'repeatPassword', 'country', 'locale', 'timezone', 'globalPosition'
        ],
        'places': [
            'address'
        ],
        'memberships': [
            'membership'
        ]
    },
    groupFieldsets: {
        general: [
            'public', 'description'
        ],
        members: [
            'members'
        ]
    },
    userstoreFieldsets: {
        general: ['name', 'remote', 'connectorName'],
        config: ['configXML'],
        admin: [ 'administrators' ]
    },

    fieldLabels: {
        membership: 'Member of'
    },

    hideFields: [ 'repeatPassword' ],
    hideProperties: [ 'newPos', 'oldPos', 'processed' ],


    /*      public      */

    compareUsers: function( newOne, oldOne, changedOnly ) {

        if ( newOne == null ) return;

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. Profile",
                    fieldsetType: "profile",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. User",
                    fieldsetType: "user",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "3. Places",
                    fieldsetType: "places",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "4. Memberships",
                    fieldsetType: "memberships",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];
        // add static properties
        var staticProps = this.getUserStaticProperties();
        if ( staticProps ) {
            props = props.concat( staticProps );
        }

        var userstores = Ext.data.StoreManager.lookup( 'Admin.store.account.UserstoreConfigStore' );
        if ( userstores ) {
            // compare based on userstore config, assuming both users are from the same store if present
            var userstoreName = ( newOne.userStore && ( !oldOne || oldOne.userStore == newOne.userStore ) ) ? newOne.userStore : this.defaultUserStore;
            var userstore = userstores.findRecord( 'name', userstoreName );
            if ( userstore && userstore.raw.userFields ) {
                props = props.concat( userstore.raw.userFields );
            }
        }

        if ( props.length == 0 ) {
            // fall back to comparing objects properties
            props = props.concat( this.collectUniqueProperties( newOne, oldOne, true ) );
        }

        if ( props.length > 0 ) {
            comparison.children = this.compareProperties( 'user', comparison.children, props, newOne, oldOne, changedOnly );

            // filter empty fieldsets
            comparison.children = Ext.Array.filter(comparison.children, function (prop, index, all) {
                if ( prop.leaf || ( !prop.leaf && prop.children && prop.children.length > 0) ) {
                    return true;
                }
                return false;
            });
        }

        return comparison;
    },

    compareUserstores: function( newOne, oldOne, changedOnly ) {
        if ( newOne == null ) return;

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. General",
                    fieldsetType: "general",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. Config",
                    fieldsetType: "config",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "3. Administrators",
                    fieldsetType: "admin",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];
        // add static properties
        var staticProps = this.getUserstoreStaticProperties();
        if ( staticProps ) {
            props = props.concat( staticProps );
        }

        if ( props.length == 0 ) {
            // fall back to comparing objects properties
            props = props.concat( this.collectUniqueProperties( newOne, oldOne, true ) );
        }

        if ( props.length > 0 ) {
            comparison.children = this.compareProperties( 'userstore', comparison.children, props, newOne, oldOne, changedOnly );

            // filter empty fieldsets
            comparison.children = Ext.Array.filter(comparison.children, function (prop, index, all) {
                if ( prop.leaf || ( !prop.leaf && prop.children && prop.children.length > 0) ) {
                    return true;
                }
                return false;
            });
        }

        return comparison;
    },

    compareGroups: function( newOne, oldOne, changedOnly ) {
        if ( newOne == null ) return;

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. General",
                    fieldsetType: "general",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. Members",
                    fieldsetType: "members",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];
        // add static properties
        var staticProps = this.getGroupStaticProperties();
        if ( staticProps ) {
            props = props.concat( staticProps );
        }

        var userstores = Ext.data.StoreManager.lookup( 'Admin.store.account.UserstoreConfigStore' );
        if ( userstores ) {
            // compare based on userstore config, assuming both users are from the same store if present
            var userstoreName = ( newOne.userStore && ( !oldOne || oldOne.userStore == newOne.userStore ) ) ? newOne.userStore : this.defaultUserStore;
            var userstore = userstores.findRecord( 'name', userstoreName );
            if ( userstore && userstore.raw.userFields ) {
                props = props.concat( userstore.raw.userFields );
            }
        }

        if ( props.length == 0 ) {
            // fall back to comparing objects properties
            props = props.concat( this.collectUniqueProperties( newOne, oldOne, true ) );
        }

        if ( props.length > 0 ) {
            comparison.children = this.compareProperties( 'group', comparison.children, props, newOne, oldOne, changedOnly );

            // filter empty fieldsets
            comparison.children = Ext.Array.filter(comparison.children, function (prop, index, all) {
                if ( prop.leaf || ( !prop.leaf && prop.children && prop.children.length > 0) ) {
                    return true;
                }
                return false;
            });
        }

        return comparison;
    },


    /*      private     */

    collectUniqueProperties: function ( newOne, oldOne, asMap ) {
        var duplicateProps = [].concat( this.collectProperties( oldOne, asMap ), this.collectProperties( newOne, asMap ) );
        return this.filterProperties( duplicateProps, asMap );
    },

    collectProperties: function( obj, asMap ) {
        var list = [];
        if ( Ext.isObject( obj ) ) {
            for( var prop in obj ) {
                if( obj.hasOwnProperty( prop ) ) {
                    list.push( asMap ? {
                        iso: false,
                        readOnly: true,
                        remote: false,
                        required: false,
                        type: prop
                    } : prop );
                }
            }
        }
        return list;
    },

    filterProperties: function( duplicateProps, asMap ) {
        var props = [];
        if ( asMap ) {
            var contains = false;
            Ext.Array.forEach( duplicateProps, function( duplicateProp ) {
                contains = false;
                Ext.Array.forEach( props, function( prop ) {
                    if ( prop.type == duplicateProp.type ) {
                        contains = true;
                        return false;
                    }
                });
                if ( !contains ) props.push( duplicateProp );
            });
        } else {
            props = Ext.Array.unique( duplicateProps );
        }
        return props;
    },

    compareProperties: function ( accountType, resultList, propertiesList, newOne, oldOne, changedOnly ) {

        Ext.Array.each( propertiesList, function( prop, index, props ) {

            if ( !this.isFieldHidden( prop.type) ) {
                var newVal = this.getValue( newOne, prop.type );
                var oldVal = this.getValue( oldOne, prop.type );
                var changeType = this.getChangeType( oldVal, newVal );

                if ( (!changedOnly || changeType !== 'none') && (!Ext.isEmpty(newVal) || !Ext.isEmpty(oldVal)) ) {
                    var item = this.createItem( prop.type, oldVal, newVal, changeType );
                    var fieldset = this.getFieldset( accountType, resultList, prop.type );
                    if ( fieldset ) {
                        if ( !Ext.isDefined( fieldset.children ) ) {
                            fieldset.children = [];
                        }
                        item = this.handleSpecialCase( prop.type, item );
                        if ( item ) {
                            fieldset.children = fieldset.children.concat( item )
                        }
                    } else {
                        Ext.Array.insert(resultList, 0, [item] );
                    }
                }
            }

        }, this);
        return resultList;
    },


    createItem: function( name, oldVal, newVal, changeType ) {
        var item;
        if( Ext.isArray( oldVal ) || Ext.isArray( newVal ) ) {
            item = {
                label: this.getLabel( name ),
                fieldType: name,
                newValue: newVal ? newVal.length + " item(s)" : undefined,
                previousValue: oldVal ? oldVal.length + " item(s)" : undefined,
                changeType: changeType,
                expanded: true,
                leaf: false,
                children: this.createArray( name, oldVal, newVal )
            };

        } else if( Ext.isObject( oldVal ) || Ext.isObject( newVal ) ) {
            item = {
                label: this.getLabel( name ),
                fieldType: name,
                newValue: newVal ? '[' + newVal['newPos'] + ']' : undefined,
                previousValue: oldVal ? '[' + oldVal['oldPos'] + ']' : undefined,
                changeType: changeType,
                expanded: true,
                leaf: false,
                children: this.createObject( oldVal, newVal )
            };
        } else {
            item = {
                label: this.getLabel( name ),
                fieldType: name,
                newValue: Ext.htmlEncode(newVal),
                previousValue: Ext.htmlEncode(oldVal),
                changeType: changeType,
                leaf: true
            };
        }
        return item;
    },

    createArray: function( name, oldOne, newOne ) {
        var list = [];
        var newVal, oldVal, changeType;

        if ( newOne ) {
            for ( var j = 0; j < newOne.length; j++ ) {
                newVal = newOne[ j ];
                oldVal = this.getCorrespondingItem( oldOne, newVal );
                changeType = this.getChangeType( oldVal, newVal );
                newVal.newPos = j;
                if ( oldVal ) {
                    oldVal.processed = true;
                }
                list.push( this.createItem( name, oldVal, newVal, changeType ) );
            }
        }
        // need to iterate over old one as well,
        // because there can be deleted items,
        // which hasn't been processed yet
        if ( oldOne ) {
            for ( var k = 0; k < oldOne.length; k++ ) {
                oldVal = oldOne[ k ];
                if ( !oldVal.processed ) {
                    newVal = this.getCorrespondingItem( newOne, oldVal );
                    changeType = this.getChangeType( oldVal, newVal );
                    if ( newVal ) {
                        newVal.newPos = Ext.Array.indexOf( newOne, newVal );
                    }
                    list.push( this.createItem( name, oldVal, newVal, changeType ) );
                }
            }
        }
        // delete processed status from the old ones
        if ( oldOne ) {
            for ( var i = 0; i < oldOne.length; i++ ) {
                delete oldOne[i].processed;
            }
        }

        return list;
    },

    createObject: function( oldOne, newOne ) {
        var list = [];
        var prop, newVal, oldVal, changeType;
        var props = this.collectUniqueProperties( newOne, oldOne );
        for( var p in props ) {
            prop = props[ p ];
            if ( !this.isPropertyHidden( prop ) ) {
                newVal = this.getValue( newOne, prop );
                oldVal = this.getValue( oldOne, prop );
                changeType = this.getChangeType( oldVal, newVal );
                list.push( this.createItem( prop, oldVal, newVal, changeType ) )
            }
        }
        return list;
    },


    handleSpecialCase: function( name, item ) {
        switch ( name ) {
            case 'address':
                // special case for addresses which need to be added directly to fieldset
                if ( !item.children || item.children.length == 0 ) {
                    return undefined;
                } else {
                    return item.children;
                }
                break;
            case 'members':
            case 'administrators':
            case 'membership':
                // special case for members which need to be shown as summary
                var added = 0, removed = 0, modified = 0;
                for ( var i = 0; i < item.children.length; i++ ) {
                    var member = item.children[i];
                    switch ( item.children[i].changeType ) {
                        case 'added': added++; break;
                        case 'removed': removed++; break;
                        case 'modified': modified++; break;
                    }
                }
                var value = item.children.length + ' Account(s)';
                if( added > 0 || removed > 0 || modified > 0 ) {
                    value += ' ('
                            + (added > 0 ? added + ' added' + ( removed > 0 || modified > 0 ? ',': '' ) : '')
                            + (removed > 0 ? removed + ' removed' + ( modified > 0 ? ',': '' ) : '' )
                            + (modified > 0 ? modified + ' modified': '' )
                            + ')';
                }
                value += ' &ndash; <a href="#" class="admin-summary-show-details-link">Details</a>'
                item.newValue = value;
                item.leaf = true;
                delete item.children;
                break;
        }
        return item;
    },

    getUserStaticProperties: function() {
        var props = [];
        if ( Ext.isDefined( Admin.view.account.EditUserFormPanel.staticFields ) ) {
            props = props.concat( Admin.view.account.EditUserFormPanel.staticFields )
        }
        props.push({
                       type: 'membership',
                       required: true,
                       remote: false,
                       readonly: false,
                       iso: true
                   });
        return props;
    },

    getUserstoreStaticProperties: function() {
        return [
            {
                type: 'name',
                required: true,
                remote: false,
                readonly: false,
                iso: true
            },
            {
                "type":"remote",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            },
            {
                "type":"connectorName",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            },
            {
                "type":"configXML",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            },
            {
                "type":"administrators",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            }
        ];
    },

    getGroupStaticProperties: function() {
        return [
            {
                type: 'displayName',
                required: true,
                remote: false,
                readonly: false,
                iso: true
            },
            {
                "type":"public",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            },
            {
                "type":"description",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            },
            {
                "type":"members",
                "readOnly":false,
                "required":false,
                "remote":false,
                "iso":true
            }
        ]
    },

    isPropertyHidden: function( name ) {
        for ( var i = 0; i < this.hideProperties.length; i++ ) {
            if( this.hideProperties[ i ] == name ) return true;
        }
        return false;
    },

    isFieldHidden: function( name ) {
        for ( var i = 0; i < this.hideFields.length; i++ ) {
            if( this.hideFields[ i ] == name ) return true;
        }
        return false;
    },

    getCorrespondingItem: function( array, oldOne ) {

        if ( !Ext.isArray( array ) || !Ext.isObject( oldOne ) ) {
            return undefined;
        }

        var position = oldOne.oldPos;
        var key = oldOne.key;
        if ( Ext.isDefined( position ) || Ext.isDefined( key ) ) {
            var newOne;
            for ( var i = 0; i < array.length; i++ ) {
                newOne = array[ i ];
                if ( ( Ext.isDefined( key ) && newOne && newOne.key == key )
                        || ( Ext.isDefined( position ) && newOne && newOne.oldPos == position ) ) {
                    return newOne;
                }
            }
        }
        return undefined;
    },

    getFieldset: function( accountType, list, name ) {
        var fieldset;
        var fieldsetName = this.getFieldsetName( accountType, name );
        if ( fieldsetName ) {
            Ext.Array.each( list, function( fs, index, all ) {
                if( fs.fieldsetType === fieldsetName ) {
                    fieldset = fs;
                    return false;
                }
            } );
        }
        return fieldset;
    },

    getFieldsetName: function( accountType, name ) {
        var fieldsetName;
        var accounts;
        if (this[accountType + 'Fieldsets'])
        {
            accounts = this[accountType + 'Fieldsets'];
        }else
        {
            accounts = this.userFieldsets;
        }
        for( var fs in accounts ) {
            Ext.Array.each( accounts[ fs ], function( f, index, all ) {
                if( f === name) {
                    fieldsetName = fs;
                    return false;
                }
            } );
            if ( fieldsetName ) break;
        }
        return fieldsetName;
    },

    getValue: function( obj, name ) {
        var result;
        if (obj ) {
            switch ( name ) {
                case 'address':
                    name = 'addresses';
                    break;
                case 'membership':
                    name = 'groups';
                    break;
            }
            var info = obj[ 'userInfo' ];
            result = info && info[ name ] ? info[ name ] : obj[ name ];
        }
        return result;
    },

    getLabel: function( name ) {
        //TODO: need to be changed for i18n
        var label = this.fieldLabels[ name ];
        if ( !label ) {
            label = Ext.isDefined( Admin.view.account.EditUserFormPanel.fieldLabels )
                    && Admin.view.account.EditUserFormPanel.fieldLabels[ name ] ?
                    Admin.view.account.EditUserFormPanel.fieldLabels[ name ] :
                    name;
        }
        return label;
    },

    getChangeType: function( oldVal, newVal ) {
        var change = "none";
        if ( !Ext.isEmpty( newVal ) && Ext.isEmpty( oldVal ) ) {
            change = 'added';
        } else if ( Ext.isEmpty( newVal) && !Ext.isEmpty( oldVal ) ) {
            change = 'removed';
        } else if ( !this.itemsEqual( newVal, oldVal ) ) {
            change = 'modified';
        }
        return change;
    },


    itemsEqual: function( newVal, oldVal ) {
        var result = true;
        if ( Ext.isArray( newVal ) && Ext.isArray( oldVal ) ) {
            result = this.arraysEqual( newVal, oldVal );
        } else if ( Ext.isObject( newVal ) && Ext.isObject( newVal ) ) {
            result = this.objectsEqual( newVal, oldVal );
        } else {
            result = Ext.encode( newVal ) === Ext.encode( oldVal );
        }
        return result;
    },

    arraysEqual: function( a, b ) {
        if ( a.length != b.length ) {
            return false;
        }
        for ( var i = 0; i < a.length; i++) {
            if ( !this.itemsEqual( a[ i ], b[ i ] ) ) {
                return false;
            }
        }
        return true;
    },

    objectsEqual: function( a, b ) {
        for( var p in a ) {
            if ( a.hasOwnProperty( p ) && !this.itemsEqual( a[ p ], b[ p ] ) ) {
                return false;
            }
        }
        for( p in b ) {
            if( b.hasOwnProperty( p ) && typeof( a[ p ] ) == 'undefined' ) {
                return false;
            }
        }
        return true;
    }

} );
