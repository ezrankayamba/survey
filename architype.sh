#!/bin/bash

prjDir=$PWD

#Create
mvn archetype:create-from-project

#Install
cd target/generated-sources/archetype/
mvn install

#Done
cd $prjDir
echo "Successfully updated the architype"
