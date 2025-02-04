# apama-streaming-analytics-connectivity-CSVCodec
Java based Connectivity Codec for converting to/from CSV for use with [Apama](https://www.apamacommunity.com/).

## Description
Converts the given event map data to the CSV format (and vice versa). For more information on the Apama Connectivity Framework, as well as Apama in general, please see [the community website](https://www.apamacommunity.com/). Furthermore, if you wish to examine this plugin in more detail, a blog describing it also exists called "creating-your-own-apama-connectivity-plugins".

## Set-up
First, ensure you have an install of the Apama engine; a free edition is available at [the community website](https://www.apamacommunity.com/). This plugin assumes the user has familiarity with the basic structure of the install, more information of which can also be found on the community site.

Building and running this sample requires access to the Correlator and Apama command line tools.

To ensure that the environment is configured correctly for Apama, all the commands below should be executed from an Apama Command Prompt, or from a shell or command prompt where the bin\apama_env script has been run (or sourced on Unix).

### To build
The CSV codec is most easily built with the Apache ANT tool from the directory containing 'build.xml':

> ant

But if you do not have access to ANT, it will need to be built manually:

For Linux:
> mkdir build_output
> javac -cp $APAMA_HOME/lib/connectivity-plugins-api.jar -d build_output src/com/apama/samples/*.java
> jar -cf build_output/csv-codec-sample.jar -C build_output .
> cp build_output/csv-codec.jar $APAMA_WORK/lib/

For Windows:
> mkdir build_output
> javac -cp %APAMA_HOME%/lib/connectivity-plugins-api.jar -d build_output src/com/apama/samples/*.java
> jar -cf build_output/csv-codec-sample.jar -C build_output .
> copy build_output\csv-codec.jar %APAMA_WORK%\lib\

A successful build will produce output files for the CSV codec:

	build-output/csv-codec-sample.jar

These should have already been copied to APAMA_WORK/lib where the correlator will load them from.

To run the sample, you will also need to have built the [File Transport](https://github.com/Cumulocity-IoT/apama-streaming-analytics-connectivity-FileTransport) to create a full connectivity chain.

## Running the sample
You can either run the sample via the [Pysys](https://pysys-test.github.io/pysys-test/) framework by invoking the tests, or by passing the yaml Connectivity configuration file to the Correlator.

When run, the sample creates a connectivity plugin chain. The chain will have the correlator at one end and to access the 'ouside world', a plugin chain must end with a Transport. For this sample we use the File Transport which can read in data from a file to be passed towards the host correlator, or write data out to a file that has come from the host correlator. Between the File Transport and the correlator is the CSV Plugin which will convert CSV data from the file to events to be processed, or vice-versa.

To run via [Pysys](https://pysys-test.github.io/pysys-test/), go to the tests directory and invoke the command: 
  
	pysys run

You can then inspect the output within the individual tests output directory

Should you wish to run directly

1. Use example files found in the test directory CSV:

> cd CSV/tests/system/CSVCodec_sys_001/Input

2. Start the Apama Correlator specifying the connectivity config file:

> correlator --connectivityConfig connectivity.yaml

3. Inject the Connectivity plugins support EPL:

> (Linux) engine_inject $APAMA_HOME/monitors/ConnectivityPluginsControl.mon $APAMA_HOME/monitors/ConnectivityPlugins.mon

> (Windows) engine_inject %APAMA_HOME%/monitors/ConnectivityPluginsControl.mon %APAMA_HOME%/monitors/ConnectivityPlugins.mon

4. Inject the Test monitor:

> engine_inject Test.mon

### Sample Output
The EPL application is sending out events to the connectivity chain, which the correlator presents as maps to the chain. The CSV codec sees these maps and renders them as CSV documents into a string payload of the message before passing it on to the transport.

Running the sample will produce the output files:

	received.evt
	output.txt

received.evt contains the events received by the correlator that have been processed by the plugin chain. output.txt is the file written by transport in CSV format of events sent from the correlator. You'll note the original input file (input.txt) had a mix of different delimiters and additional whitespace; the codec wrote out a more conventional CSV file using only commas.
 
## License
Copyright (c) 2024 Cumulocity GmbH. The name Cumulocity GmbH and all Cumulocity GmbH product names are either trademarks or registered trademarks of Cumulocity GmbH and/or its subsidiaries and/or its affiliates and/or their licensors. Other company and product names mentioned herein may be trademarks of their respective owners. 

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. 
See the License for the specific language governing permissions and limitations under the License.

______________________
These tools are provided as-is and without warranty or support. They do not constitute part of the product suite. Users are free to use, fork and modify them, subject to the license agreement. While we welcome contributions, we cannot guarantee to include every contribution in the main project.
_____________
Contact us at [Apama community](https://apamacommunity.com) if you have any questions.
