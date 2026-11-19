exports.execute = function (name, args, invocation) {
    if (invocation) {
        invocation.progress(0, 'Started');
    }
    if (args.kind === 'returned') {
        return {
            content: [{type: 'text', text: 'Visible failure for the model'}],
            isError: true
        };
    }
    if (args.kind === 'script') {
        throw new Error('secret-js boom');
    }
    testInstance.fail(args.kind);
    return {content: []};
};
