In order to build this project you need NetBeans 6.9.1 and its included JavaFX SDK.

The JavaFX SDK has to be modified:
- make a copy of the supplied JavaFX SDK
- delete all files from the copied SDK containing the string "jogl" (use file search)
- remove the occurences of "jogl-common.jar" and "jogl-awt.jar" from profile file "desktop.properties"
