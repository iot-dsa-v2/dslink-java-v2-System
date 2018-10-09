# dslink-java-v2-System

* Version: 1.0.0
* Java - version 1.6 and up.
* [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Overview

This is a System link that provides System Information

If you are not familiar with DSA and links, an overview can be found at
[here](http://iot-dsa.org/get-started/how-dsa-works).

This link was built using the DSLink Java SDK which can be found
[here](https://github.com/iot-dsa-v2/sdk-dslink-java-v2).


## Link Architecture

This section outlines the hierarchy of nodes defined by this link.

- _SystemDslink_ - The root node of the link, displays some system hardware information and has an action for command line operations.
  - _SystemNetworkNetfaceNode_ - A node representing network related information.
  - _(Enabled on condition based)DiagnosticModeNode_ - A node representing all connected downstreams dslinks, memory and open file information.


## SystemDSLink

- This is the root node of the link. It creates below described parameters as nodes and display those information/values.
    - `Architecture` : Provides 64 bit or 32 bit details
    - `Battery Level` : Displays the battery percentage of system
    - `Cpu Usage` : Displays the overall cpu usage.
    - `Diagnostic Mode` : On providing appropriate pid file path, it displays memory usage and open file information of all connected downstreams dslinks.
    - `Disk Usage` : Displays disk usage information
    - `Free Disk Space` : Displays the free disk space memory
    - `Free Memory` : Displays free memory
    - `Hardware Identifier` : NA
    - `Host Name` : Displays the host name of system
    - `Memory Usage` : Displays how much memory has been used.
    - `Model` : model of system
    - `Open Files` : Displays open files count
    - `Operating System` : Displays which OS used in system
    - `Platform` : Displays the platform of system
    - `Poll Rate` : Based on poll rate (seconds), the system information is updated in metric panel.
    - `processes` : Displays how many processes are running
    - `Processor Count` : Display the processor count
    - `Processor Model` : Displays the processor model of system
    - `System Time` : Displays the current time of system
    - `Total Disk Space` : Displays complete hard disk space (all partition)
    - `Total Memory` : Displays total memory of RAM
    - `Used Disk Space` : Displays how much disk space is used
    - `Used Memory` : Displays the RAM memory used
    
**Actions**

- *Execute Command*  : System administration utility commands can be excuted.
  - `Command` : Required. Command (Ex: ipconfig/ifconfig).
  - `Output` : Displays the response of requested query/command. 
  - `ExitCode` : return value of the executed command
- *Execute Command Stream* : System administration utility commands can be excuted and result can be seen in table format.
  - `Command` : Required. Command (Ex: ipconfig/ifconfig).
  - `Output`- : Displays the response of requested query/command in table format.

- Mac:
  - *Run Apple Scriptm* : Executes Apple scripts which can be used to automate actions on Macintosh computers.
    - `Script` : Required. Any apple scripts (Ex: say "Hello, world!"/display dialog "Hello, world!").
    - `Output`- : Returns the response from scrips excecuted.
- Windows:
  - *Read WMIC Data* : Executes WMIC commands which allows users to performs Windows Management Instrumentation (WMI) operations with a command prompt.
    - `Script` : Required. Any WMIC commands (Ex: CONTEXT/OS).
    - `Output`- : Returns the response from command excecuted in table format.
  


_Child Nodes_

## SystemNetworkInterfaceNode

- This is the child node of root node of the link.  It creates required nodes to display Network information.
    - `awdl0` - `en0` - `en5` - `lo0` - `utun0`


## DiagnosticModeNode

This is the conditioned child node of root node of the link.  It will be created only when appropriate pid file path given in Diagnostic Mode.
It displays memory usage and open files of all connected downstream dslinks.


## Acknowledgements

SDK-DSLINK-JAVA

This software contains unmodified binary redistributions of
[sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2), which is licensed
and available under the Apache License 2.0. An original copy of the license agreement can be found
at https://github.com/iot-dsa-v2/sdk-dslink-java-v2/blob/master/LICENSE

## History

* Version 1.0.0
  - First Release

