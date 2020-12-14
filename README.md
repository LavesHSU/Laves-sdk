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

Before you build and release a new version of the LAVESDK you have to adjust the version
information. Please open the corresponding properties file of LAVESDK (location:
src\lavesdk\resources\files\lavesdk.properties) and change the major number (version_major)
and/or the minor number (version_minor) of the version.
The major number has to be increased when there are breaking changes which lead to incompatibility
with previous versions. The minor number is incremented when only new features or significant fixes
have been added to LAVESDK.
The minimum version information must be adjusted if you add additional methods to the
interface AlgorithmPlugin or the class AlgorithmRTE because these are fundamental items of the
LAVESDK. So you have to set min_version_major and min_version_minor to the values of version_major
and version_minor. Otherwise you should refrain from changing the minimum version information!

After that you have to use the build script to build the new LAVESDK release. Go to
build\build.xml (inside of your IDE like Eclipse) and run the ant task "build" or
"build_debug" (in Eclipse this is done by right-clicking on the named entry in the Outline view and
select Run As -> Ant Build).
For debugging support you have to publish a debug version with every release of the LAVESDK, too.

# Create new views or GUI components

If you create a new view component please note the "LAVESDK Visualization Policy". For
further information on "how to create a new view" look at the Javadoc of the class View.


# Visualization Policy

LAVESDK uses Swing framework to display the graphical user interface of algorithms or in
general to display the GUI. Because each algorithm has its own runtime environment (RTE) which runs
in its own thread calls to the UI must be synchronized. Swing is espacially not thread-safe thus developers must
keep in mind to ensure thread-safety when handling GUI tasks.
To reduce the work of a plugin developer, visualization components that are shipped with LAVESDK are thread-safe
by themselves. So when you create a new visualization component IN LAVESDK please consider thread-safety for ease of use
in algorithm visualizations.

Moreover, the LAVESDK brings along functionality to access GUI components in a thread-safe way. Please take a look at the classes 
GuiJob and GuiRequest (in the package lavesdk.gui) which provide mechanisms to perform thread-safe GUI tasks.