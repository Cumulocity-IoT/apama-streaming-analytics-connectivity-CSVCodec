__pysys_title__   = r""" CSV Codec input and output system test """
#                        ================================================================================

__pysys_purpose__ = r""" Reads CSV data from input.txt file and converts to com.apama.samples.Test event sent to the correlator.
	Reflect these events back out to the CSV plugin to be converted back to a CSV file.
	"""

__pysys_authors__ = ""

# Copyright (c) 2017, 2021 Cumulocity GmbH, Duesseldorf, Germany and/or its affiliates and/or their licensors. 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
# Sample PySys testcase

from pysys.constants import *
from pysys.basetest import BaseTest
from apama.correlator import CorrelatorHelper

class PySysTest(BaseTest):

	def execute(self):
		# since we can't specify locations other than APAMA_WORK/HOME and cwd, 
		# have to copy input file to output dir
		import shutil
		shutil.copyfile(self.input+'/'+'input.txt', self.output+'/'+'input.txt')
		
		# Can specify which port the correlator runs on using '-X CORR_PORT=15903' on the
		# PySys command line else it will be randomly allocated
		correlator = CorrelatorHelper(self, name='testcorrelator', port=self.CORR_PORT if hasattr(self, 'CORR_PORT') else None)
		correlator.start(arguments=['--connectivityConfig', self.input+'/connectivity.yaml'])
		correlator.receive('received.evt', channels=['received'])
		correlator.injectMonitorscript(self.project.APAMA_HOME+'/monitors/ConnectivityPluginsControl.mon')
		correlator.injectMonitorscript(self.project.APAMA_HOME+'/monitors/ConnectivityPlugins.mon')
		correlator.injectMonitorscript('Test.mon')

		# wait until the output is on disk
		self.waitForGrep('received.evt', expr="com")
		self.waitForGrep('output.txt', expr="com")

	def validate(self):
		self.assertGrep('testcorrelator.out', expr=' (ERROR|FATAL) ', contains=False)
		self.assertDiff('received.evt', 'ref-received.evt')
		self.assertDiff('output.txt', 'ref-output.txt')
