# Release 1.5
- Known bugs fixed

# Release 1.4
- RandomGraphToolBarExtension: new toolbar extension to create randomly generated graphs
- ToolBarExtension: may now be added to the main menu of the host application

# Release 1.3
- LegendView: font is now adjustable by user like in TextAreaView
- GraphView: description for directed property in properties window, repaint mechanism reworked
- DefaultRNView: additional constructor
- Sandbox: validation of the used SDK version information of a plugin
- LanguageFile: getLabel() returns now the labelID if no suitable language is found for a label and the default value is null
- Network: correction of the excess calculation of nodes
- AlgorithmStep: Bugs fixed concerning the LaTeX formula parsing of a step text
- AlgorithmRTE: it is now possible to step back from the end of the algorithm if the option „pause before stop“ is enabled
- VertexOnlyTransferProtocol: now it is posible to only transfer the vertex positions
- GraphUtils: new findShortestPaths methods to determine the shortest paths from every vertex to all other vertices in a graph
- CompleteGraphToolBarExtension/CompleteBipartiteGraphToolBarExtension: added functionality to specify the maximum weight of edge if a graph is created (the weights of the edges are determined randomly up to the maximum)

# Release 1.2
- New AlgorithmPlugin properties getAuthorContact() and getText()
- New host communication methods to request some properties of installed plugins
- New method in AlgorithmExercise to display last failed hint messages
- New transition object that enables the developer to integrate visual transitions into an algorithm visualization (predefined transitions: color transition, linear position transition to move an object along a straight)
- Network: getResidualNetwork() returns now a new type instance that is based on a multi graph to comply with the definition of a residual network
- DefaultRNView: new default view to handle residual networks graphically with caption adoption feature (that means during the creation of a residual network the captions of the vertices are automatically adopted from the network if a certain visual pattern matches)
- GraphView: during a multiselect of vertices the vertices cannot be moved anymore to prevent the user from deselecting all vertices when he accidentally moves the cursor
- GraphView: it is now possible to repaint a graph view from a subclass and to add additional tools to the tool set
- ExercisesListView: new result status icon for a failed exercise that is clickable and displays a hint that should answer why the exercise has failed
- Known Bugs fixed

# Release 1.1
- Bug fixes in the runtime environment of the algorithms and the host system
- New option for exercises to display input hints
- MatrixEditor: it is now possible to remove elements with the delete key
- ExecutionTable: cell content is selected when user wants to edit a cell so that the new value can directly be entered using the keyboard