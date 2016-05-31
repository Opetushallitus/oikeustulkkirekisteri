#!/bin/bash
if [ ! -d "node" ]; then
	mvn clean install
fi
if [ ! -d "src/main/webapp/node_modules/webpack-dev-server" ]; then
	mvn clean install	
fi
cd src/main/webapp/
../../../node/node/node node_modules/webpack/bin/webpack.js --watch &
../../../node/node/node dev.js
