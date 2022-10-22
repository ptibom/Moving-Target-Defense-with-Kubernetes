# Moving Target Defense with Kubernetes
This tool is a working prototype that was part of a Master's thesis project at Chalmers University of Technology (CTH), Sweden, by Philip Tibom and Max Buck under supervision of Ahmed Ali-Eldin Hassan. The thesis will be linked to from this page when it is ready.

The tool swaps applications / pods between different Kubernetes nodes in order to achieve a moving target defense. See the master's thesis for more information.

## Thesis
The thesis is currently hosted in this repository while awaiting publication by Chalmers Library.  
[View Thesis](https://raw.githubusercontent.com/ptibom/Moving-Target-Defense-with-Kubernetes/3c6e1ffc3401f7bcb01a55437691357a2895634f/documentation/MTD%20Master%20Thesis%20220918.pdf)

## How to build
The project is created using Maven.  
Run `mvn package` to create a standalone snapshot.

We recommend compiling without the unit tests because the unit tests requires a running kubernetes cluster to pass all the tests.
```
mvn package -Dmaven.test.skip
```

Alternatively, open the project in IntelliJ and run `MtdMain.java`

## How to use
The application can be started in two ways.

1. Run the jar file from CLI. `java -jar MtdJava-1.0-SNAPSHOT.jar`  
This opens up a CLI menu with a setup wizard. The menu can also load an existing settings file.
2. Run without a Menu. `java -jar MtdJava-1.0-SNAPSHOT.jar <FILENAME>` replace  `<filename>` with a filename. The file must be a settings yaml file and it is loaded and executed immedietly.

## Documentation and Code Structure
The project designed using the MVC architecture. See the thesis and documentations folder on the repository for diagrams and more information.

Javadocs are available here:  
https://ptibom.github.io/Moving-Target-Defense-with-Kubernetes/documentation/javadoc/

The cluster setup and labling are described here:  
https://github.com/ptibom/Moving-Target-Defense-with-Kubernetes/blob/master/documentation/kubernetes-cluster.md

https://github.com/ptibom/Moving-Target-Defense-with-Kubernetes/blob/master/documentation/kubernetes-labels.md

You may use any setup that you want. We have also tested with Minikube and DigitalOcean's preconfigured kubernetes cluster. Just make sure that there are no other pods/deployments running under the default namespace. Multiple namespace support is not yet fully implemented.

MTD algorithm V2 and V3 both work but with different ideas on how to manage load balancing. V3 works better but can curently not delete old deployments while restarting the system. Old deployments must be purged manually using kubectl, while V2 cleans up previous deployments automatically.

## Depedencies
* Java 11 or later
* Maven
* Official Java Client by Kubernetes (included with Maven)

## Attributions
* Supervisor Ahmed Ali-Eldin Hassan and examiner Vincenzo Massimiliano Gulisano.
* AI Sweden, provided a work place, seminars and support.
* SNIC - Swedish National Infrastructure for Computing, provided cloud resources for development and testing.

## License (GPLv3)
<pre>
Moving Target Defense with Kubernetes
Copyright (C) 2022  Philip Tibom and Max Buck

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
</pre>
