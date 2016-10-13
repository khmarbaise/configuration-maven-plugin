/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.*
import java.util.*


t = new IntegrationBase()

def getProjectVersion(File basedir) {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    return pom.version
}

def projectVersion = getProjectVersion(basedir);

println "Project version: ${projectVersion}"

def classifierList = ['dev-01', 'qa-01' ]

def buildLogFile = new File( basedir, "build.log");

if (!buildLogFile.exists()) {
    throw new FileNotFoundException("build.log does not exists.")
}

def targetDirectory = new File (basedir, "target")
if (!targetDirectory.exists()) {
    throw new FileNotFoundException("target directory does not exists.")
}

def result = true

classifierList.each { classifier ->
    def tf = new File (targetDirectory, "filtering-test-" + projectVersion + "-" + classifier + ".jar")
    println "Checking ${classifier}: " + tf.getAbsolutePath()
    if (!tf.exists()) {
        throw new FileNotFoundException("The file " + tf.getAbsolutePath() + " does not exists.")
    }

    def contentOfFirstPropertiesFileFromArchive = t.getLinesFromFileWithinTheArchive(tf, 'first.properties')

    def foundClassifier = classifier in contentOfFirstPropertiesFileFromArchive 
    def foundVersion = projectVersion in contentOfFirstPropertiesFileFromArchive

    if (!foundClassifier) {
      println "The classifier '${classifier}' couldn't be found in the " 
      println "content of the 'first.properties' file within the archive ${tf.getAbsolutePath()}."
      result = false
    }
    if (!foundVersion) {
      println "The projectVersion '${projectVersion}' couldn't be found in the "
      println "content of the 'first.properties' file within the archive ${tf.getAbsolutePath()}."
      result = false
    }
}

return result;
