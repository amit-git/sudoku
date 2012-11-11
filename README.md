When running the app or the tests, if you get the following error 

"Cannot load 32-bit SWT libraries on 64-bit JVM"

It means that you have to add -d32 as a VM argument before you 
can use swt.jar on a MAC OS which is built for 32 bit JVM.

