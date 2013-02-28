/**
 * requested to be in separated file.
 * used in ContentWizardPanel.js
 */
! function( window ) {
    // script for safe evaluation in the context.
    // usage example: evaluateContentDisplayNameScript("$('firstName') + ' ' +  $('lastName')", { "firstName": "Thomas", "lastName": "Andersen" } )
    function safeEval( script, context ) {
        function $ ( arg ) {
            var value = context[arg];
            return value ? value : '';
        }

        var result = '';

        try {
            // hide eval, Function, document, window and other things from the script.
            result = eval( 'var eval; var Function; var document; var location; var Ext; ' +
                     'var window; var parent; var self; var top; ' +
                     script ) ;
        } catch (e) {
            console.error('cannot evaluate [' + script + '] function.');
        }

        return result;
    }

    window.evaluateContentDisplayNameScript = safeEval;
} ( window );
