In order to build this project you need NetBeans 6.9.1 and its included JavaFX SDK.

The JavaFX SDK has to be modified since it includes an outdated version of JOGL:
- make a copy of the supplied JavaFX SDK
- delete all files from the copied SDK containing the string "jogl" (use file search)
- remove the occurences of "jogl-common.jar" and "jogl-awt.jar" from profile file "desktop.properties"

Then add the modified JavaFX SDK under the name
JavaFX_SDK_without_JOGL_on_Java_1.6+
as a JavaFX platform in netbeans and use it in the JFXSurfer project.
