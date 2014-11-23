cv-backgamon
============

1. Install the extension "m2e" to your copy of eclipse. 
2. Clone this project using Git to a location of your choice (I use `Documents\GitHub\cv-backgammon`)
    1. Consider using [Github for Windows](https://windows.github.com/) if on Windows. 
3. Install OpenCV on your machine v2.4.10, preferably to `C:\OpenCV\`
    1. [OpenCV Homepage](http://opencv.org/)
    1. [OpenCV Download Page](https://sourceforge.net/projects/opencvlibrary/files/opencv-win/2.4.10/)
4. Import cloned project into eclipse
    1. Package Explorer > Right Click > Import > Existing Project into Workspace
5. Link the new OpenCV Java Bindings file to your new eclipse project
    1. Right Click on project > Build Path > Configure Build Path
    2. Add External Jar
    3. Select opencv-2410.jar from `C:\OpenCV\build\java`
6. Compile and Run!


Useful Resources
================
[Tutorials Point](http://www.tutorialspoint.com/java_dip/eroding_dilating.htm)
Great site with a ton of references to OpenCV Java code. Link to reference article: "Eroding Dilating"
