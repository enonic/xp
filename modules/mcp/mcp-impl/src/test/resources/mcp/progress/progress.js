exports.execute = function (name, args, invocation) {
    var supported = invocation ? invocation.isProgressSupported() : false;
    var completed = 0;

    if (invocation) {
        invocation.progress(0);
        invocation.progress(0.25, 'Starting');
        invocation.progress(0.5, args.steps);
    }

    for (var step = 1; step <= args.steps; step++) {
        completed++;
        if (invocation) {
            invocation.progress(completed, args.steps, 'Step ' + completed);
        }
    }

    return {
        content: [{type: 'text', text: 'Completed ' + completed + ' steps'}],
        structuredContent: {
            completedSteps: completed,
            progressSupported: supported
        },
        isError: false
    };
};
