# Middleware API

[![Build Status](https://travis-ci.org/POPBL-6/middleware.svg?branch=master)](https://travis-ci.org/POPBL-6/middleware)
[![Coverity Status](https://scan.coverity.com/projects/8890/badge.svg)](https://scan.coverity.com/projects/popbl-6-middleware)

## What this is
This is a middleware API that allows communication between applications using a publisher/subscriber model.
It currently has plain TCP and SSL implementations in Java,

## How to use it
See the <a href=https://github.com/POPBL-6/middleware/wiki/2-.-How-to-configure-the-Middleware>wiki</a>.

## Example project
Check out <a href=https://github.com/POPBL-6/PBL6_Prueba>this</a> project.

## Building the API
We have included a gradle build script to ease the process.
Just run the following command to build the project
```sh
$ gradle build
```

If you want to export the middleware and it's dependencies, all in JAR format, compressed in a Zip file, execute following command.
```sh
$ gradle distZip
```

