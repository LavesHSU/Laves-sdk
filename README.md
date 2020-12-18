# Logistics Algorithms Visualization and Education Software Development Kit

The LAVESDK is a Java and Swing based API (application programming interface) to develop
plugins for the software [LAVES](https://github.com/LavesHSU/Laves).

The API brings a broad range of functionality to develop tools that aim at supporting
non-mathematics and non-informatics students in understanding the basic concepts of algorithms.
By default the LAVESDK provides several didactical methods like step-by-step visualization,
animation and interactive teaching by use of an exercise system.

The SDK is mainly designed for OR-related (Operations Research) courses and brings along
functionality to visualize algorithm texts (including LaTeX expressions), graph data structures,
execution tables, matrices, etc. but it is easily extendable by custom implementations to address
the specific needs of a course.

# Contributors
[![MIS Uni Siegen](https://www.uni-siegen.de/stylesheets/redesign_09/uni_images/uni_logo.svg)](https://www.wiwi.uni-siegen.de/mis/software/laves.html)
[![HSU](https://www.hsu-hh.de/wp-content/themes/hsu/img/hsulogo.png)](https://www.hsu-hh.de/or/)

# Build and release a new version

Before you build and release a new version of the LAVESDK please adjust the version
information in the appropriate properties file of LAVESDK (location:
src\lavesdk\resources\files\lavesdk.properties). Change the major number (version_major)
and/or the minor number (version_minor) of the SDK.
The major number has to be increased when there are breaking changes which lead to incompatibility
with previous versions. The minor number is incremented when only new features or significant bug fixes
have been added to LAVESDK.

When there are breaking changes in the signature of interfaces, classes or methods, like AlgorithmPlugin or AlgorithmRTE, you need
to adjust the minimum version information, too. The minimum version information indicates that only plugins that use the same or a higher version of the SDK are compatible.

Afterwards, use the Ant build script (location: build\build.xml) to compile the SDK and create a new release.
It is recommended to always provide a release and a debug version. Therefore use the corresponding build targets "build" and "build_debug" of the build script.

Please see (and extend) the changelog for a What's new list.

# Visualization Policy

LAVESDK uses Swing framework to display the graphical user interface of algorithms or in
general to display the GUI. Because each algorithm has its own runtime environment (RTE) which runs
in its own thread calls to the UI must be synchronized. Swing is espacially not thread-safe thus developers must
keep in mind to ensure thread-safety when handling GUI tasks to avoid memory consistency errors or unexpected behaviour.
To reduce the work of a plugin developer, visualization components that are shipped with LAVESDK are thread-safe
by themselves. So when you create a new visualization component that is contained in LAVESDK please consider thread-safety for ease of use
in algorithm visualizations.

Moreover, the LAVESDK brings along functionality to access GUI components in a thread-safe way. Please take a look at the classes 
GuiJob and GuiRequest (in the package lavesdk.gui) which provide mechanisms to perform thread-safe GUI tasks.