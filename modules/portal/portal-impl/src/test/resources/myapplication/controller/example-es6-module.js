/**
 * Example demonstrating how uppercase HTTP method names solve the issues:
 * 
 * Issue 1: 'delete' is a reserved word in JavaScript
 * - Old way: exports.delete = function() {...}  // Syntax error in strict mode
 * - New way: exports.DELETE = function() {...}  // No conflict!
 * 
 * Issue 2: Importing functions with same name as HTTP methods
 * - Old way with conflict:
 *   import {get} from '/lib/xp/content';
 *   exports.get = function() {...}  // Name collision!
 * 
 * - New way without conflict:
 *   import {get} from '/lib/xp/content';
 *   exports.GET = function() {
 *     const content = get({key: '...'});  // No collision!
 *   }
 */

// Import content library without naming conflicts
import {get as getContent} from '/lib/xp/content';  // Old workaround
// OR with uppercase methods:
import {get} from '/lib/xp/content';  // Can use 'get' directly now!

// Uppercase method names (recommended)
export function GET(req) {
    // Can use imported 'get' function without conflicts
    const content = get({key: req.params.contentId});
    return {
        status: 200,
        body: content
    };
}

export function POST(req) {
    return {
        status: 201,
        body: 'Created'
    };
}

// No more 'delete' reserved word issues!
export function DELETE(req) {
    return {
        status: 200,
        body: 'Deleted'
    };
}

export function PUT(req) {
    return {
        status: 200,
        body: 'Updated'
    };
}

export function PATCH(req) {
    return {
        status: 200,
        body: 'Patched'
    };
}

// Fallback for other methods
export function ALL(req) {
    return {
        status: 200,
        body: 'Fallback for ' + req.method
    };
}

// Note: Lowercase methods still work for backward compatibility
// exports.get, exports.post, exports.delete (CommonJS style)
// export function get, export function post, etc. (ES6 module style)
