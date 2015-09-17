
# Admin UI Module

This module contains all frontend source.

## Building the UI

For now, we are using Grunt for building javascript sources. Please install Grunt (grunt-cli) in NPM before using it.
After installing Grunt, run the following command to install Grunt dependencies:

    npm install

Then, to compile TypeScript, type the following:

    grunt typescript

## Generating API documentation

To generated API documentation, just write the following:

    grunt typedoc
    
The documentation will be written to 'target/typedoc' folder. We have not added this to the normal build process yet since
it takes very long time to generate. When we get this to work on the typescript definition files instead we will add it so
it will generate the doc automatically on every build.

